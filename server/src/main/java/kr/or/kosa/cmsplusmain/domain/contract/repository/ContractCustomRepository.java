package kr.or.kosa.cmsplusmain.domain.contract.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.dto.SortPageDto;
import kr.or.kosa.cmsplusmain.domain.base.repository.BaseCustomRepository;
import kr.or.kosa.cmsplusmain.domain.contract.dto.ContractSearchReq;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import lombok.extern.slf4j.Slf4j;

import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContract.contract;
import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContractProduct.contractProduct;
import static kr.or.kosa.cmsplusmain.domain.member.entity.QMember.member;
import static kr.or.kosa.cmsplusmain.domain.payment.entity.QPayment.payment;
import static kr.or.kosa.cmsplusmain.domain.vendor.entity.QVendor.vendor;

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
	public List<Contract> findContractListWithCondition(String vendorUsername, ContractSearchReq search,
		PageReq pageReq) {
		return jpaQueryFactory
			.selectFrom(contract)

			.join(contract.vendor, vendor)
			.join(contract.member, member).fetchJoin()
			.leftJoin(contract.contractProducts, contractProduct).on(contractProductNotDel())    // left join
			.join(contract.payment, payment).fetchJoin()

			.where(
				contractNotDel(),                                // 계약 소프트 삭제

				vendorUsernameEq(vendorUsername),                // 고객 일치

				memberNameContains(search.getMemberName()),        // 회원 이름 포함
				memberPhoneContains(search.getMemberPhone()),    // 회원 휴대번호 포함
				contractDayEq(search.getContractDay()),            // 약정일 일치
				contractStatusEq(search.getContractStatus()),    // 계약상태 일치
				consentStatusEq(search.getConsentStatus())        // 동의상태 일치
			)

			.groupBy(contract.id)
			.having(
				productNameContainsInGroup(search.getProductName()),
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
	 * 계약 상세 조회
	 * */
	public Optional<Contract> findContractDetailById(Long id) {
		return Optional.ofNullable(jpaQueryFactory
			.selectFrom(contract)
			.join(contract.payment, payment).fetchJoin()
			.where(
				contractNotDel(),
				contract.id.eq(id))
			.fetchOne());
	}

	/*
	 * 회원 상세 조회 - 계약리스트
	 * */
	public List<Contract> findContractListItemByMemberId(String Username, Long memberId, SortPageDto.Req pageable) {
		return jpaQueryFactory
				.selectFrom(contract)
				.join(contract.member, member)
				.join(contract.vendor, vendor)
				.leftJoin(contract.contractProducts, contractProduct).on(contractProductNotDel())
				.where(
						vendorUsernameEq(Username),
						member.id.eq(memberId),
						contractNotDel()
				)
				.offset(pageable.getPage())
				.limit(pageable.getSize())
				.fetch();
	}

	/*
	 * 회원 상세 조회 - 계약리스트 수
	 * */
	public int countContractListItemByMemberId(String Username, Long memberId) {
		Long res = jpaQueryFactory
			.select(contract.id.countDistinct())
			.from(contract)
			.join(contract.member, member)
			.join(contract.vendor, vendor)
			.where(
				vendorUsernameEq(Username),
				member.id.eq(memberId),
				contractNotDel()
			)
			.fetchOne();

		return (res != null) ? res.intValue() : 0;
	}

	/* 
	* 고객과 계약 id 일치하는 계약 존재 여부
	* */
	public boolean isExistContractByUsername(Long contractId, String vendorUsername) {
		Integer res = jpaQueryFactory
			.selectOne()
			.from(contract)
			.join(contract.vendor, vendor)
			.where(
				contract.id.eq(contractId),
				contractNotDel(),
				vendorUsernameEq(vendorUsername)
			)
			.fetchOne();
		return res != null;
	}

	public int countAllContracts(String vendorUsername, ContractSearchReq search) {
		Long count = jpaQueryFactory
			.select(contract.id.countDistinct())
			.from(contract)

			.join(contract.vendor, vendor)
			.join(contract.member, member)
			.leftJoin(contract.contractProducts, contractProduct).on(contractProductNotDel())    // left join
			.join(contract.payment, payment)

			.where(
				contractNotDel(),                                // 계약 소프트 삭제

				vendorUsernameEq(vendorUsername),                // 고객 일치

				memberNameContains(search.getMemberName()),        // 회원 이름 포함
				memberPhoneContains(search.getMemberPhone()),    // 회원 휴대번호 포함
				contractDayEq(search.getContractDay()),            // 약정일 일치
				contractStatusEq(search.getContractStatus()),    // 계약상태 일치
				consentStatusEq(search.getConsentStatus())        // 동의상태 일치
			)

			.groupBy(contract.id)
			.having(
				productNameContainsInGroup(search.getProductName()),
				contractPriceLoeInGroup(search.getContractPrice())
			)
			.fetchOne();

		return (count != null) ? count.intValue() : 0;
	}
}
