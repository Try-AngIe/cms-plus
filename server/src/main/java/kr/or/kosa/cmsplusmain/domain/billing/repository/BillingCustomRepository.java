package kr.or.kosa.cmsplusmain.domain.billing.repository;

import static kr.or.kosa.cmsplusmain.domain.billing.entity.QBilling.*;
import static kr.or.kosa.cmsplusmain.domain.billing.entity.QBillingProduct.*;
import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContract.*;
import static kr.or.kosa.cmsplusmain.domain.member.entity.QMember.*;
import static kr.or.kosa.cmsplusmain.domain.payment.entity.QPayment.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.repository.BaseCustomRepository;
import kr.or.kosa.cmsplusmain.domain.billing.dto.request.BillingSearchReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.BillingListItemRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.QBillingListItemRes;
import kr.or.kosa.cmsplusmain.domain.billing.entity.Billing;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingProduct;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
@Repository
public class BillingCustomRepository extends BaseCustomRepository<Billing> {

	public BillingCustomRepository(EntityManager em, JPAQueryFactory jpaQueryFactory) {
		super(em, jpaQueryFactory);
	}

	/**
	 * 청구목록 조회 |
	 * 기본정렬: 생성시간 내림차순
	 * */
	public List<BillingListItemRes> findBillingListWithCondition(Long vendorId, BillingSearchReq search,
		PageReq pageReq) {
		return jpaQueryFactory
			.select(new QBillingListItemRes(
				billing.id,
				member.name,
				member.phone,
				billingProduct.price.longValue().multiply(billingProduct.quantity).sum(),
				billing.billingStatus,
				payment.paymentType,
				billing.billingDate,
				getFirstProductName(search.getProductName()),
				billingProduct.countDistinct().intValue()
			))
			.from(billing)
			.join(billing.contract, contract)
			.join(contract.member, member)
			.join(contract.payment, payment)
			.leftJoin(billing.billingProducts, billingProduct).on(billingProduct.deleted.isFalse())
			.where(
				billingNotDel(),
				contractVendorIdEq(vendorId),
				memberNameContains(search.getMemberName()),
				memberPhoneContains(search.getMemberPhone()),
				billingStatusEq(search.getBillingStatus()),
				paymentTypeEq(search.getPaymentType()),
				billingDateEq(search.getBillingDate()),
				productNameContainsInBilling(search.getProductName()),
				contractIdEq(search.getContractId())
			)
			.groupBy(billing.id, member.name, member.phone, billing.billingStatus, payment.paymentType,
				billing.billingDate)
			.having(billingPriceLoe(search.getBillingPrice()))
			.orderBy(buildOrderSpecifier(pageReq).orElse(billing.createdDateTime.desc()))
			.limit(pageReq.getSize())
			.offset(pageReq.getPage())
			.fetch();
	}

	/**
	 * 고객의 전체 계약 수 (검색 조건 반영된 계약 수)
	 * */
	public long countBillingListWithCondition(Long vendorId, BillingSearchReq search) {
		return jpaQueryFactory
			.select(billing.id)
			.from(billing)
			.join(billing.contract, contract)
			.join(contract.member, member)
			.join(contract.payment, payment)
			.leftJoin(billing.billingProducts, billingProduct).on(billingProduct.deleted.isFalse())
			.where(
				billingNotDel(),
				contractVendorIdEq(vendorId),
				memberNameContains(search.getMemberName()),
				memberPhoneContains(search.getMemberPhone()),
				billingStatusEq(search.getBillingStatus()),
				paymentTypeEq(search.getPaymentType()),
				billingDateEq(search.getBillingDate()),
				productNameContainsInBilling(search.getProductName()),
				contractIdEq(search.getContractId())
			)
			.groupBy(billing.id)
			.having(billingPriceLoe(search.getBillingPrice()))
			.fetchCount();
	}

	/*
	 * 고객의 계약 존재 여부
	 * */
	public boolean isExistBillingByUsername(Long billingId, Long vendorId) {
		Integer res = jpaQueryFactory
			.selectOne()
			.from(billing)
			.join(billing.contract, contract)
			.where(
				contract.vendor.id.eq(vendorId),
				billing.id.eq(billingId),
				billingNotDel()
			)
			.fetchOne();

		return res != null;
	}

	/*
	 * 청구 상세
	 * */
	public Billing findBillingWithContract(Long billingId) {
		return jpaQueryFactory
			.selectFrom(billing)
			.join(billing.contract, contract).fetchJoin()
			.join(contract.member, member).fetchJoin()
			.join(contract.payment, payment).fetchJoin()
			.where(
				billingNotDel(),
				billing.id.eq(billingId)
			)
			.fetchOne();
	}

	public List<BillingProduct> findAllBillingProducts(Long billingId) {
		return jpaQueryFactory
			.selectFrom(billingProduct)
			.where(
				billingProduct.billing.id.eq(billingId),
				billingProductNotDel()
			)
			.fetch();
	}

	/*
	 * 계약 상세 - 청구목록
	 *
	 * 최신순 정렬
	 * */
	public List<Billing> findBillingsByContractId(Long contractId, PageReq pageable) {
		return jpaQueryFactory
			.selectFrom(billing)
			.join(billing.contract, contract).fetchJoin()
			.join(contract.member, member).fetchJoin()
			.join(contract.payment, payment).fetchJoin()
			.where(
				billingNotDel(),
				contract.id.eq(contractId)
			)
			.orderBy(billing.createdDateTime.desc())
			.offset(pageable.getPage())
			.limit(pageable.getSize())
			.fetch();
	}

	/*
	 * 회원 삭제 - 청구 목록 조회
	 * */
	public List<Billing> findBillingListByMemberId(Long vendorId, Long memberId) {
		return jpaQueryFactory
			.selectFrom(billing)
			.join(billing.contract, contract)
			.where(
				contract.vendor.id.eq(vendorId),
				contract.member.id.eq(memberId),
				contractNotDel(),
				billingNotDel()
			)
			.fetch();
	}

	/**
	 * 회원의 완납 건을 제외한 청구 수
	 * */
	public int countInProgressBillingsByMember(Long memberId) {
		Long res = jpaQueryFactory
			.select(billing.countDistinct())
			.from(billing)
			.join(billing.contract, contract)
			.where(
				contract.member.id.eq(memberId),
				billing.deleted.isFalse(),
				billing.billingStatus.ne(BillingStatus.PAID)
			)
			.fetchOne();
		return (res != null) ? res.intValue() : 0;
	}

	/*
	 * 회원 상세 - 기본정보(청구수)
	 * */
	public int findBillingStandardByMemberId(Long vendorId, Long memberId) {
		Long res = jpaQueryFactory
			.select(billing.id.countDistinct())
			.from(billing)
			.join(billing.contract, contract)
			.where(
				contract.vendor.id.eq(vendorId),
				contract.member.id.eq(memberId),
				contractNotDel(),
				billingNotDel()
			)
			.fetchOne();

		return (res == null) ? 0 : res.intValue();
	}

	/*
	 * 회원 상세 - 기본정보(청구금액)
	 * */
	public Long findBillingPriceByMemberId(Long vendorId, Long memberId) {
		return jpaQueryFactory
			.select(billingProduct.price.longValue().multiply(billingProduct.quantity).sum())
			.from(billingProduct)
			.join(billingProduct.billing, billing)
			.join(billing.contract, contract)
			.where(
				contract.vendor.id.eq(vendorId),
				contract.member.id.eq(memberId),
				contractNotDel(),
				billingProductNotDel()
			)
			.fetchOne();
	}

	/*
	 * 계약의 청구 전체 개수
	 * */
	public int countAllBillingsByContract(Long contractId) {
		Long res = jpaQueryFactory
			.select(billing.id.countDistinct())
			.from(billing)
			.where(
				billingNotDel(),
				billing.contract.id.eq(contractId)
			)
			.fetchOne();

		return (res != null) ? res.intValue() : 0;
	}

	/*
	 * 계약의 청구 총 개수 및 금액
	 * */
	public Long[] findBillingCntAndPriceByContract(Long contractId) {
		List<Long> billingIds = jpaQueryFactory
			.select(billing.id)
			.from(billing)
			.where(
				billingNotDel(),
				billing.contract.id.eq(contractId)
			)
			.fetch();

		if (billingIds == null || billingIds.isEmpty()) {
			return new Long[] {0L, 0L};
		}

		Long totalPrice = jpaQueryFactory
			.select(billingProduct.price.longValue().multiply(billingProduct.quantity).sum())
			.from(billingProduct)
			.where(
				billingProductNotDel(),
				billingProduct.billing.id.in(billingIds)
			)
			.fetchOne();

		return new Long[] {(long)billingIds.size(), totalPrice};
	}

	/*********** 조건 ************/

	private BooleanExpression paymentTypeEq(PaymentType paymentType) {
		return (paymentType != null) ? payment.paymentType.eq(paymentType) : null;
	}

	private BooleanExpression billingNotDel() {
		return billing.deleted.isFalse();
	}

	private BooleanExpression contractVendorIdEq(Long vendorId) {
		return (vendorId != null) ? contract.vendor.id.eq(vendorId) : null;
	}

	private BooleanExpression billingStatusEq(BillingStatus billingStatus) {
		return (billingStatus != null) ? billing.billingStatus.eq(billingStatus) : null;
	}

	private BooleanExpression billingDateEq(LocalDate billingDate) {
		return (billingDate != null) ? billing.billingDate.eq(billingDate) : null;
	}

	private StringExpression getFirstProductName(String productName) {
		if (StringUtils.hasText(productName)) {
			return Expressions.stringTemplate(
				"COALESCE({0}, {1})",
				JPAExpressions
					.select(Expressions.stringTemplate("ANY_VALUE({0})", billingProduct.name))
					.from(billingProduct)
					.where(billingProduct.billing.eq(billing),
						billingProduct.name.contains(productName))
					.groupBy(billingProduct.billing.id),
				billingProduct.name.min()
			);
		} else {
			return billingProduct.name.min();
		}
	}

	private BooleanExpression productNameContainsInBilling(String productName) {
		if (!StringUtils.hasText(productName)) {
			return null;
		}
		return billing.id.in(
			JPAExpressions
				.select(billingProduct.billing.id)
				.from(billingProduct)
				.where(billingProduct.name.contains(productName))
		);
	}

	private BooleanExpression billingPriceLoe(Long billingPrice) {
		return billingPrice != null ? billingProduct.price.multiply(billingProduct.quantity).sum().loe(billingPrice) :
			null;
	}

	private BooleanExpression contractIdEq(Long contractId) {
		return contractId != null ? contract.id.eq(contractId) : null;
	}
}
