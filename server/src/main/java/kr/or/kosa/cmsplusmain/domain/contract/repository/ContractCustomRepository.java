package kr.or.kosa.cmsplusmain.domain.contract.repository;

import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContract.*;
import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContractProduct.*;
import static kr.or.kosa.cmsplusmain.domain.member.entity.QMember.*;
import static kr.or.kosa.cmsplusmain.domain.payment.entity.QPayment.*;
import static kr.or.kosa.cmsplusmain.domain.payment.entity.type.QPaymentTypeInfo.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.repository.BaseCustomRepository;
import kr.or.kosa.cmsplusmain.domain.contract.dto.request.ContractSearchReq;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ContractCustomRepository extends BaseCustomRepository<Contract> {

	public ContractCustomRepository(EntityManager em, JPAQueryFactory jpaQueryFactory) {
		super(em, jpaQueryFactory);
	}

	/*
	 * 계약 목록 조회
	 *
	 * 총 3번의 쿼리가 발생
	 * 1.
	 *  */
	public List<Contract> findContractListWithCondition(Long vendorId, ContractSearchReq search,
		PageReq pageReq) {
		return jpaQueryFactory
			.selectFrom(contract)

			.join(contract.member, member).fetchJoin()
			.join(contract.payment, payment).fetchJoin()
			.leftJoin(contract.contractProducts, contractProduct).on(contractProductNotDel())    // left join

			.where(
				contractNotDel(),                                // 계약 소프트 삭제

				contract.vendor.id.eq(vendorId),                // 고객 일치
				contractStatusEq(search.getContractStatus()),
				memberNameContains(search.getMemberName()),        // 회원 이름 포함
				memberPhoneContains(search.getMemberPhone()),    // 회원 휴대번호 포함
				contractDayEq(search.getContractDay()),         // 약정일 일치
				paymentTypeEq(search.getPaymentType()),
				paymentMethodEq(search.getPaymentMethod())
			)

			.groupBy(contract.id)
			.having(
				// productNameContainsInGroup(search.getProductName()),
				contractPriceLoeInGroup(search.getContractPrice())
			)

			.orderBy(
				buildOrderSpecifier(pageReq)
					.orElse(contract.createdDateTime.desc())
			)

			.offset(pageReq.getPage())
			.limit(pageReq.getSize())
			.fetch();
	}

	/*
	 * 전체 계약 수
	 * */
	public int countAllContracts(Long vendorId, ContractSearchReq search) {
		JPQLQuery<Long> subQuery = jpaQueryFactory
			.select(contract.id)
			.from(contract)

			.join(contract.member, member)
			.join(contract.payment, payment)
			.leftJoin(contract.contractProducts, contractProduct).on(contractProductNotDel())

			.where(
				contractNotDel(),                                // 계약 소프트 삭제

				contract.vendor.id.eq(vendorId),                // 고객 일치

				memberNameContains(search.getMemberName()),        // 회원 이름 포함
				memberPhoneContains(search.getMemberPhone()),    // 회원 휴대번호 포함
				contractDayEq(search.getContractDay()),         // 약정일 일치
				paymentTypeEq(search.getPaymentType()),            // 결제방식 일치
				paymentMethodEq(search.getPaymentMethod())        // 결제수단 일치
			)

			.groupBy(contract.id)
			.having(
				// productNameContainsInGroup(search.getProductName()),
				contractPriceLoeInGroup(search.getContractPrice())
			);

		Long count = jpaQueryFactory
			.select(contract.id.countDistinct())
			.from(contract)
			.where(contract.id.in(subQuery))
			.fetchOne();

		return (count != null) ? count.intValue() : 0;
	}

	/*
	 * 계약 상세 조회
	 * */
	public Contract findContractDetailById(Long id) {
		return jpaQueryFactory
			.selectFrom(contract)
			.join(contract.payment, payment).fetchJoin()
			.join(contract.member, member).fetchJoin()
			.join(payment.paymentTypeInfo, paymentTypeInfo).fetchJoin()
			.where(
				contractNotDel(),
				contract.id.eq(id))
			.fetchOne();
	}

	/*
	 * 회원 상세 조회 - 계약리스트
	 * */
	public List<Contract> findContractListItemByMemberId(Long vendorId, Long memberId, PageReq pageReq) {
		return jpaQueryFactory
			.selectFrom(contract)
			.where(
				contract.vendor.id.eq(vendorId),
				contract.member.id.eq(memberId),
				contractNotDel()
			)
			.offset(pageReq.getPage())
			.limit(pageReq.getSize())
			.fetch();
	}

	/*
	 * 회원 상세 조회 - 계약리스트 수
	 * */
	public int countContractListItemByMemberId(Long vendorId, Long memberId) {
		Long res = jpaQueryFactory
			.select(contract.id.countDistinct())
			.from(contract)
			.where(
				contract.vendor.id.eq(vendorId),
				contract.member.id.eq(memberId),
				contractNotDel()
			)
			.fetchOne();

		return (res != null) ? res.intValue() : 0;
	}

	/*
	 * 고객과 계약 id 일치하는 계약 존재 여부
	 * */
	public boolean isExistContractByUsername(Long contractId, Long vendorId) {
		Integer res = jpaQueryFactory
			.selectOne()
			.from(contract)
			.where(
				contract.vendor.id.eq(vendorId),
				contract.id.eq(contractId),
				contractNotDel()
			)
			.fetchOne();
		return res != null;
	}

	private BooleanExpression paymentTypeEq(PaymentType paymentType) {
		return (paymentType != null) ? payment.paymentType.eq(paymentType) : null;
	}

	private BooleanExpression paymentMethodEq(PaymentMethod paymentMethod) {
		return (paymentMethod != null) ? payment.paymentMethod.eq(paymentMethod) : null;
	}

	private BooleanExpression contractStatusEq(ContractStatus contractStatus) {
		return (contractStatus != null) ? contract.contractStatus.eq(contractStatus) : null;
	}
}
