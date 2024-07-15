package kr.or.kosa.cmsplusmain.domain.billing.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.dto.SortPageDto;
import kr.or.kosa.cmsplusmain.domain.base.repository.BaseCustomRepository;
import kr.or.kosa.cmsplusmain.domain.billing.dto.BillingSearchReq;
import kr.or.kosa.cmsplusmain.domain.billing.entity.Billing;
import lombok.extern.slf4j.Slf4j;

import static kr.or.kosa.cmsplusmain.domain.billing.entity.QBilling.billing;
import static kr.or.kosa.cmsplusmain.domain.billing.entity.QBillingProduct.billingProduct;
import static kr.or.kosa.cmsplusmain.domain.billing.entity.QBillingStandard.billingStandard;
import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContract.contract;
import static kr.or.kosa.cmsplusmain.domain.member.entity.QMember.member;
import static kr.or.kosa.cmsplusmain.domain.payment.entity.QPayment.payment;
import static kr.or.kosa.cmsplusmain.domain.product.entity.QProduct.product;
import static kr.or.kosa.cmsplusmain.domain.vendor.entity.QVendor.vendor;

@Slf4j
@Repository
public class BillingCustomRepository extends BaseCustomRepository<Billing> {

	public BillingCustomRepository(EntityManager em, JPAQueryFactory jpaQueryFactory) {
		super(em, jpaQueryFactory);
	}

	/*
	 * 청구목록 조회
	 *
	 * 총 2번의 쿼리
	 * 1. billing
	 * 2. billingProduct <- batchsize 100
	 * */
	public List<Billing> findBillingListWithCondition(String vendorUsername, BillingSearchReq search,
		PageReq pageReq) {
		return jpaQueryFactory
			.selectFrom(billing)

			.join(billing.billingStandard, billingStandard).fetchJoin()
			.leftJoin(billingStandard.billingProducts, billingProduct).on(billingProductNotDel())    // left join
			.join(billingProduct.product, product)
			.join(billingStandard.contract, contract).fetchJoin()
			.join(contract.vendor, vendor)
			.join(contract.member, member).fetchJoin()
			.join(contract.payment, payment).fetchJoin()

			.where(
				billingNotDel(),                                // 청구 소프트 삭제

				vendorUsernameEq(vendorUsername),                // 고객 아이디 일치

				memberNameContains(search.getMemberName()),        // 회원 이름 포함
				memberPhoneContains(search.getMemberPhone()),    // 회원 휴대전화 포함
				billingStatusEq(search.getBillingStatus()),        // 청구상태 일치
				paymentTypeEq(search.getPaymentType()),            // 결제방식 일치
				billingDateEq(search.getBillingDate())            // 결제일 일치
			)

			.groupBy(billing.id)
			.having(
				productNameContainsInGroup(search.getProductName()),    // 청구상품 이름 포함
				billingPriceLoeInGroup(search.getBillingPrice())        // 청구금액 이하
			)

			.orderBy(
				buildOrderSpecifier(pageReq)
					.orElse(billing.createdDateTime.desc())				// 기본 정렬 = 생성시간 내림차순
			)

			.offset(pageReq.getPage())
			.limit(pageReq.getSize())
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
			.join(billing.billingStandard, billingStandard).fetchJoin()
			.join(billingStandard.contract, contract).fetchJoin()
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
	* 청구 상세
	* */
	public Billing findBillingDetail(Long billingId) {
		return jpaQueryFactory
			.selectFrom(billing)
			.join(billing.billingStandard, billingStandard).fetchJoin()
			.join(billingStandard.contract, contract).fetchJoin()
			.join(contract.member, member).fetchJoin()
			.join(contract.payment, payment).fetchJoin()
			.where(
				billingNotDel(),
				billing.id.eq(billingId)
			)
			.fetchOne();
	}

	/*
	* 청구와 청구기준 같이 조회
	* 청구 상품 수정시 사용됨
	* */
	public Billing findBillingWithStandard(Long billingId) {
		return jpaQueryFactory
			.selectFrom(billing)
			.join(billing.billingStandard, billingStandard).fetchJoin()
			.where(
				billingNotDel(),
				billing.id.eq(billingId)
			)
			.fetchOne();
	}

	/*
	 * 회원 상세 - 기본정보(청구수)
		select
			count(distinct member1.id)
		from
			BillingStandard billingStandard
		inner join
			contract.vendor as vendor
		inner join
			contract.member as member1
		inner join
			billingStandard.contract as contract
		where
			vendor.username = ?1
			and member1.id = ?2
			and contract.deleted = ?3
			and billingStandard.deleted = ?4
	 * */
	public int findBillingStandardByMemberId(String username, Long memberId){
		Long res = jpaQueryFactory
				.select(member.id.countDistinct()).from(billingStandard)
				.join(contract.vendor, vendor)
				.join(contract.member, member)
				.join(billingStandard.contract, contract)
				.where(
						vendorUsernameEq(username),
						member.id.eq(memberId),
						contractNotDel(),
						billingStandardNotDel()
				)
				.fetchOne();

		return (res == null) ? 0 : res.intValue();
	}

	/*
	 * 계약의 청구 전체 개수
	 * */
	public int countAllBillingsByContract(Long contractId) {
		Long res = jpaQueryFactory
			.select(billing.id.count())
			.from(billing)
			.join(billing.billingStandard, billingStandard)
			.join(billingStandard.contract, contract)
			.join(contract.member, member)
			.join(contract.payment, payment)
			.where(
				billingNotDel(),
				contract.id.eq(contractId)
			)
			.fetchOne();

		return (res != null) ? res.intValue() : 0;
	}

	/*
	* 고객의 계약 존재 여부
	* */
	public boolean isExistBillingByUsername(Long billingId, String vendorUsername) {
		Integer res = jpaQueryFactory
			.selectOne()
			.from(billing)
			.join(billing.billingStandard, billingStandard)
			.join(billingStandard.contract, contract)
			.join(contract.vendor, vendor)
			.where(
				billing.id.eq(billingId),
				billingNotDel(),
				vendorUsernameEq(vendorUsername)
			)
			.fetchOne();

		return res != null;
	}

	/*
	* 고객의 전체 계약 수 (검색 조건 반영된 계약 수)
	* */
	public int countAllBillings(String vendorUsername, BillingSearchReq search) {
		Long count = jpaQueryFactory
				.select(billing.id.countDistinct())
				.from(billing)

				.join(billing.billingStandard, billingStandard)
				.leftJoin(billingStandard.billingProducts, billingProduct).on(billingProductNotDel())    // left join
				.join(billingProduct.product, product)
				.join(billingStandard.contract, contract)
				.join(contract.vendor, vendor)
				.join(contract.member, member)
				.join(contract.payment, payment)

				.where(
						billingNotDel(),                                // 청구 소프트 삭제

						vendorUsernameEq(vendorUsername),                // 고객 아이디 일치

						memberNameContains(search.getMemberName()),        // 회원 이름 포함
						memberPhoneContains(search.getMemberPhone()),    // 회원 휴대전화 포함
						billingStatusEq(search.getBillingStatus()),        // 청구상태 일치
						paymentTypeEq(search.getPaymentType()),            // 결제방식 일치
						billingDateEq(search.getBillingDate())            // 결제일 일치
				)

				.groupBy(billing.id)
				.having(
						productNameContainsInGroup(search.getProductName()),    // 청구상품 이름 포함
						billingPriceLoeInGroup(search.getBillingPrice())        // 청구금액 이하
				)

				.fetchOne();

		return (count != null) ? count.intValue() : 0;
	}

	/*
	 회원 상세 - 기본정보(청구금액)
	    select
			sum(billingProduct.price * billingProduct.quantity)
		from
			BillingProduct billingProduct
		inner join
			billingProduct.billingStandard as billingStandard
		inner join
			billingStandard.contract as contract
		inner join
			contract.vendor as vendor
		inner join
			contract.member as member1
		where
			vendor.username = ?1
			and member1.id = ?2
			and contract.deleted = ?3
			and billingStandard.deleted = ?4
	  */
	public Long findBillingProductByMemberId(String username, Long memberId){
		Long res = jpaQueryFactory
				.select(billingProduct.price.longValue().multiply(billingProduct.quantity).sum())
				.from(billingProduct)
				.join(billingProduct.billingStandard,billingStandard)
				.join(billingStandard.contract, contract)
				.join(contract.vendor, vendor)
				.join(contract.member, member)
				.where(
						vendorUsernameEq(username),
						member.id.eq(memberId),
						contractNotDel(),
						billingStandardNotDel()
				)
				.fetchOne();
		return res;
	}


}
