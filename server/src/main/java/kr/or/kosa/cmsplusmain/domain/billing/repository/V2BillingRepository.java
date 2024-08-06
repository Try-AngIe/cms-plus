package kr.or.kosa.cmsplusmain.domain.billing.repository;

import static kr.or.kosa.cmsplusmain.domain.billing.entity.QBilling.*;
import static kr.or.kosa.cmsplusmain.domain.billing.entity.QBillingProduct.*;
import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContract.*;
import static kr.or.kosa.cmsplusmain.domain.member.entity.QMember.*;
import static kr.or.kosa.cmsplusmain.domain.payment.entity.QPayment.*;
import static kr.or.kosa.cmsplusmain.util.QueryExpressions.*;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;

import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.repository.V2BaseRepository;
import kr.or.kosa.cmsplusmain.domain.billing.dto.request.BillingSearchReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.BillingListItemRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.QBillingListItemRes;
import kr.or.kosa.cmsplusmain.domain.billing.entity.Billing;

@Repository
public class V2BillingRepository extends V2BaseRepository<Billing, Long> {

	public Billing findById(Long id) {
		return selectFrom(billing)
			.where(billing.id.eq(id))
			.fetchOne();
	}

	/**
	 * 삭제된 청구도 조회된다.
	 * 요구사항: 청구서 조회 시 삭제 여부를 회원에게 알릴 수 있다.
	 * */
	public Billing findByIdIncludingDeleted(Long billingId) {
		return selectWithDel(billing)
			.from(billing)
			.where(billing.id.eq(billingId))
			.fetchOne();
	}

	/**
	 * 청구목록 검색 |
	 * 기본정렬: 생성시간 내림차순
	 * */
	public List<BillingListItemRes> searchBillings(Long vendorId, BillingSearchReq search, PageReq pageReq) {

		JPAQuery<BillingListItemRes> query = searchQuery(vendorId, search)
			.select(new QBillingListItemRes(
				billing.id,
				member.name,
				member.phone,
				billing.billingPrice,
				billing.billingStatus,
				payment.paymentType,
				billing.billingDate,
				firstProductNameSubQuery(search.getProductName()),
				billing.billingProductCnt
			))
			.orderBy(buildOrderSpecifier(pageReq));

		return applyPaging(query, pageReq).fetch();
	}

	/**
	 * 고객의 전체 계약 수 (검색 조건 반영된 계약 수)
	 * */
	public Long countSearchedBillings(Long vendorId, BillingSearchReq search) {
		Long res = searchQuery(vendorId, search).select(billing.count()).fetchOne();
		return res == null ? 0 : res;
	}

	/**
	 * 검색 조건 쿼리 생성
	 * 조인이 다 수 걸리므로,
	 * 리턴값이 BillingListItemRes 인 경우만 사용한다. (리턴 정보가 조인이 필수적인 경우)
	 * */
	private JPAQuery<?> searchQuery(Long vendorId, BillingSearchReq search) {
		return from(billing)
			.join(billing.contract, contract)
			.join(contract.member, member)
			.join(contract.payment, payment)
			.where(
				contractVendorIdEq(vendorId),                            // 고객 일치
				memberNameContains(search.getMemberName()),                // 회원명 포함
				memberPhoneContains(search.getMemberPhone()),            // 휴대전화 포함
				billingStatusEq(search.getBillingStatus()),                // 청구상태 일치
				billingPriceLoe(search.getBillingPrice()),                // 청구금액 이하
				paymentTypeEq(search.getPaymentType()),                    // 결제방식 일치
				billingDateEq(search.getBillingDate()),                    // 결제일 일치
				firstProductNameSubQuery(search.getProductName()).isNotNull(),    // 상품명 포함
				contractIdEq(search.getContractId())                    // 계약 ID 일치 (계약 상세 청구목록)
			);
	}

	/**
	 * 고객이 해당 청구를 가지고 있는지 확인
	 * */
	public boolean existByVendorId(Long billingId, Long vendorId) {
		if (billingId == null || vendorId == null) {
			return false;
		}
		Integer res = selectOneFrom(billing)
			.from(billing)
			.join(billing.contract, contract)
			.where(contract.vendor.id.eq(vendorId), billing.id.eq(billingId))
			.fetchOne();

		return res != null;
	}

	/**
	 * 청구 상세 조회를 위한 계약, 회원, 결제 fetch join
	 * */
	public Billing findBillingWithContract(Long billingId) {
		return selectWithNotDel(billing, billing)
			.from(billing)
			.join(billing.contract, contract).fetchJoin()
			.join(contract.member, member).fetchJoin()
			.join(contract.payment, payment).fetchJoin()
			.where(billing.id.eq(billingId))
			.fetchOne();
	}

	/*********** 조건 ************/

	private BooleanExpression productNameContains(String productName) {
		return StringUtils.hasText(productName) ? billingProduct.name.contains(productName) : null;
	}

	private BooleanExpression billingPriceLoe(Long billingPrice) {
		return billingPrice != null ? billing.billingPrice.loe(billingPrice) : null;
	}

	private JPAQuery<String> firstProductNameSubQuery(String productName) {
		// 검색어를 프론트에 표시할 상품 이름에 표시하기 위한 서브쿼리
		// Querydsl 에서는 서브쿼리에 limit 조건이 반영 안되므로 min 사용
		return from(billingProduct)
			.select(billingProduct.name.min())
			.where(
				billingProduct.billing.eq(billing),
				productNameContains(productName));
	}

	private OrderSpecifier<?> buildOrderSpecifier(PageReq pageReq) {
		if (pageReq == null || !StringUtils.hasText(pageReq.getOrderBy())) {
			return billing.createdDateTime.desc();
		}

		String orderBy = pageReq.getOrderBy();

		if (orderBy.equals("billingPrice")) {
			NumberExpression<Long> expression = billing.billingPrice;
			return pageReq.isAsc() ? expression.asc() : expression.desc();
		}

		return billing.createdDateTime.desc();
	}
}
