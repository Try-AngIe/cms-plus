package kr.or.kosa.cmsplusmain.domain.contract.repository;

import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContract.*;
import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContractProduct.*;
import static kr.or.kosa.cmsplusmain.domain.member.entity.QMember.*;
import static kr.or.kosa.cmsplusmain.domain.payment.entity.QPayment.*;
import static kr.or.kosa.cmsplusmain.util.QueryExpressions.*;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;

import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.repository.V2BaseRepository;
import kr.or.kosa.cmsplusmain.domain.contract.dto.request.ContractSearchReq;
import kr.or.kosa.cmsplusmain.domain.contract.dto.response.QV2ContractListItemRes;
import kr.or.kosa.cmsplusmain.domain.contract.dto.response.V2ContractListItemRes;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class V2ContractRepository extends V2BaseRepository<Contract, Long> {

	/**
	 * 계약목록 검색 |
	 * */
	public List<V2ContractListItemRes> searchContracts(Long vendorId, ContractSearchReq search, PageReq pageReq) {
		JPAQuery<V2ContractListItemRes> query = searchQuery(vendorId, search)
			.select(new QV2ContractListItemRes(
				contract.id, member.name, member.phone, contract.contractDay,
				contract.contractPrice,
				payment.paymentType, contract.contractStatus,
				firstProductNameSubQuery(search.getProductName()),
				contract.contractProductCnt
			))
			.orderBy(buildOrderSpecifier(pageReq));

		return applyPaging(query, pageReq).fetch();
	}

	/**
	 * 고객의 전체 계약 수 (검색 조건 반영된 계약 수)
	 * */
	public Long countSearchedContracts(Long vendorId, ContractSearchReq search) {
		Long res = searchQuery(vendorId, search).select(contract.count()).fetchOne();
		return res == null ? 0 : res;
	}

	/**
	 * 검색 조건 생성
	 * */
	private JPAQuery<?> searchQuery(Long vendorId, ContractSearchReq search) {
		return from(contract)
			.join(contract.member, member)
			.join(contract.payment, payment)
			.where(
				contractVendorIdEq(vendorId),                    // 고객의 계약
				contractStatusEq(search.getContractStatus()),    // 계약 상태 일치
				contractPriceLoe(search.getContractPrice()),    // 계약 금액 이하
				contractDayEq(search.getContractDay()),            // 계약일 일치
				memberNameContains(search.getMemberName()),        // 회원명 포함
				memberPhoneContains(search.getMemberPhone()),    // 휴대전화 포함
				paymentTypeEq(search.getPaymentType()),            // 결제방식 일치
				paymentMethodEq(search.getPaymentMethod()),        // 결제수단 일치
				firstProductNameSubQuery(search.getProductName()).isNotNull(),    // 상품명 포함
				memberIdEq(search.getMemberId())                // 회원 ID 일치 (회원 상세 계약목록)
			);
	}

	/**
	 * 해당 고객에게 계약이 존재하는지 여부
	 * */
	public boolean existsContractByVendorId(Long vendorId, Long contractId) {
		if (contractId == null || vendorId == null) {
			return false;
		}

		Integer res = selectOneFrom(contract)
			.where(
				contractVendorIdEq(vendorId),
				contractIdEq(contractId)
			)
			.fetchOne();
		return res != null;
	}

	/*********** 조건 ************/
	private BooleanExpression productNameContains(String productName) {
		return StringUtils.hasText(productName) ? contractProduct.name.contains(productName) : null;
	}

	/**
	 * @deprecated groupby 절 삭제로, 일반 서브쿼리 대체
	 * */
	private StringExpression getFirstProductName(String productName) {
		if (StringUtils.hasText(productName)) {
			return Expressions.stringTemplate(
				"COALESCE({0}, {1})",
				JPAExpressions
					.select(Expressions.stringTemplate("ANY_VALUE({0})", contractProduct.name))
					.from(contractProduct)
					.where(contractProduct.contract.eq(contract),
						contractProduct.name.contains(productName))
					.groupBy(contractProduct.contract.id),
				contractProduct.name.min()
			);
		} else {
			return contractProduct.name.min();
		}
	}

	private BooleanExpression contractPriceLoe(Long contractPrice) {
		return contractPrice != null ? contract.contractPrice.loe(contractPrice) : null;
	}

	/**
	 * @deprecated where subquery -> select subquery 변경됨
	 * */
	@Deprecated
	private BooleanExpression productNameContainsInContract(String productName) {
		if (!StringUtils.hasText(productName)) {
			return null;
		}
		return contract.id.in(
			JPAExpressions
				.select(contractProduct.contract.id)
				.from(contractProduct)
				.where(contractProduct.name.contains(productName))
		);
	}

	private JPAQuery<String> firstProductNameSubQuery(String productName) {
		// 검색어를 프론트에 표시할 상품 이름에 표시하기 위한 서브쿼리
		// Querydsl 에서는 서브쿼리에 limit 조건이 반영 안되므로 min 사용
		return from(contractProduct)
			.select(contractProduct.name.min())
			.where(
				contractProduct.contract.eq(contract),
				productNameContains(productName));
	}

	private OrderSpecifier<?> buildOrderSpecifier(PageReq pageReq) {
		if (pageReq == null || !StringUtils.hasText(pageReq.getOrderBy())) {
			return contract.createdDateTime.desc();
		}

		String orderBy = pageReq.getOrderBy();

		switch (orderBy) {
			case "contractPrice" -> {
				NumberExpression<Long> expression = contract.contractPrice;
				return pageReq.isAsc() ? expression.asc() : expression.desc();
			}
			case "contractDay" -> {
				NumberExpression<Integer> expression = contract.contractDay;
				return pageReq.isAsc() ? expression.asc() : expression.desc();
			}
			case "memberName" -> {
				StringExpression expression = member.name;
				return pageReq.isAsc() ? expression.asc() : expression.desc();
			}
		}

		return contract.createdDateTime.desc();
	}
}
