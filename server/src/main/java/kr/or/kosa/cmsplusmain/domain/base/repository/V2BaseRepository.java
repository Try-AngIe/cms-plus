package kr.or.kosa.cmsplusmain.domain.base.repository;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEntity;

@Transactional(readOnly = true)
@Repository
public abstract class V2BaseRepository<E extends BaseEntity, ID> {

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private JPAQueryFactory queryFactory;
	private JpaEntityInformation<E, ID> entityInformation;	// lazy loading

	/**
	 * 엔티티 신규 저장 및 수정
	 * */
	@Transactional
	public E save(E entity) {
		if (isNewEntity(entity)) {
			em.persist(entity);
			return entity;
		}

		return em.merge(entity);
	}

	/**
	 * 일반적으로 transaction (readOnly=false)로 jpa dirty-check 로 하는 것을 권장
	 * 다 수 엔티티 수정으로 bulkUpdate가 필요한 경우에 사용
	 * 수정시각(BaseEntity.modifiedDateTime) 자동 반영 안되므로 수정시간 직접 설정 필요
	 * */
	protected final JPAUpdateClause update(EntityPath<? extends BaseEntity> entity) {
		return queryFactory.update(entity);
	}

	/**
	 * 파라미터로 넘긴 엔티티의 삭제 조건을 같이 검사한다.
	 * */
	protected final JPAQuery<?> from(EntityPath<? extends BaseEntity> entity) {
		return queryFactory.from(entity).where(isNotDeleted(entity));
	}
	protected final JPAQuery<E> selectFrom(EntityPath<E> entity) {
		return queryFactory.selectFrom(entity).where(isNotDeleted(entity));
	}
	protected final JPAQuery<Integer> selectOneFrom(EntityPath<? extends BaseEntity> entity) {
		return queryFactory.selectOne().from(entity).where(isNotDeleted(entity));
	}

	/**
	 * 삭제(소프트 딜리트) 검사를 할 항목을 두번쨰 인자[배열]로 준다.
	 * 청구를 select 하고, [청구, 계약, 청구상품]을 삭제된 것은 빼고 보여주고 싶다. => select(billing, billing, contract, billingProduct)
	 * 삭제 여부를 검사할 엔티티는 조인 항목에 포함되어야 한다.
	 * */
	@SafeVarargs
	protected final <T> JPAQuery<T> selectWithNotDel(Expression<T> expr, EntityPath<? extends BaseEntity>... notDelEntities) {
		if (notDelEntities == null || notDelEntities.length == 0) {
			return queryFactory.select(expr);
		}
		return withNotDel(queryFactory.select(expr), notDelEntities);
	}

	protected final <T> JPAQuery<T> selectWithDel(Expression<T> expr) {
		return queryFactory.select(expr);
	}

	/**
	 * 페이지네이션 적용
	 * */
	protected final <T> JPAQuery<T> applyPaging(JPAQuery<T> query, PageReq pageReq) {
		return query
			.offset(pageReq.getPage())
			.limit(pageReq.getSize());
	}

	/**
	 * 소프트 삭제 여부
	 * */
	protected final BooleanExpression isNotDeleted(Expression<? extends BaseEntity> entity) {
		if (entity == null) return null;
		try {
			BooleanPath deletedPath = (BooleanPath) entity.getClass().getField("deleted").get(entity);
			return deletedPath.isFalse();
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("소프트 딜리트 대상 엔티티가 아닙니다: " + entity.getType().getSimpleName(), e);
		}
	}

	@SafeVarargs
	private <T> JPAQuery<T> withNotDel(JPAQuery<T> query, EntityPath<? extends BaseEntity>... notDelEntities) {
		if (notDelEntities == null || notDelEntities.length == 0) {
			return query;
		}
		BooleanExpression notDelConditions = Arrays.stream(notDelEntities)
			.filter(Objects::nonNull)
			.map(this::isNotDeleted)
			.reduce(BooleanExpression::and)
			.orElse(null);

		return query.where(notDelConditions);
	}

	@SuppressWarnings("unchecked")
	private JpaEntityInformation<E, ID> getJpaEntityInformation(Class<?> clazz) {
		if (this.entityInformation == null) {
			this.entityInformation =
				(JpaEntityInformation<E, ID>) JpaEntityInformationSupport.getEntityInformation(clazz, em);
		}

		return this.entityInformation;
	}

	private Boolean isNewEntity(E entity) {
		return this.getJpaEntityInformation(entity.getClass()).isNew(entity);
	}
}