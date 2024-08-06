package kr.or.kosa.cmsplusmain.domain.dashboard.repository;

import static kr.or.kosa.cmsplusmain.domain.billing.entity.QBilling.*;
import static kr.or.kosa.cmsplusmain.domain.billing.entity.QBillingProduct.*;
import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContract.*;
import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContractProduct.*;
import static kr.or.kosa.cmsplusmain.domain.member.entity.QMember.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.or.kosa.cmsplusmain.domain.billing.entity.Billing;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingStatus;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.BillingStatQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.ContractStatQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.DayBillingQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.MemberStatQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.QBillingStatQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.QContractStatQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.QDayBillingQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.QMemberStatQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.QRecentFiveContractQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.QTopFiveMemberQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.RecentFiveContractQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.TopFiveMemberQueryRes;
import kr.or.kosa.cmsplusmain.domain.member.entity.MemberStatus;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StatRepository {

	private final JPAQueryFactory queryFactory;

	/**
	 * 회원 현황
	 * 전체, 신규, 활성
	 * */
	public MemberStatQueryRes findMemberStat(Long vendorId, int year, int month) {
		// 신규 회원 수
		NumberExpression<Long> newMemberCase = new CaseBuilder()
			.when(member.enrollDate.year().eq(year).and(member.enrollDate.month().eq(month)))
			.then(Expressions.asNumber(1).castToNum(Long.class))
			.otherwise(0L);

		// 활성 회원 수
		NumberExpression<Long> activeMemberCase = new CaseBuilder()
			.when(member.status.eq(MemberStatus.ENABLED))
			.then(Expressions.asNumber(1).castToNum(Long.class))
			.otherwise(0L);

		return queryFactory
			.select(new QMemberStatQueryRes(
				member.id.count(),
				newMemberCase.sum(),
				activeMemberCase.sum()))
			.from(member)
			.where(member.vendor.id.eq(vendorId))
			.fetchOne();
	}

	/**
	 * 계약 현황
	 * 전체, 신규(계약 시작), 만료예정
	 * */
	public ContractStatQueryRes findContractStat(Long vendorId, int year, int month) {
		// 신규
		NumberExpression<Long> newContractCase = new CaseBuilder()
			.when(contract.contractStartDate.year().eq(year).and(contract.contractStartDate.month().eq(month)))
			.then(Expressions.asNumber(1).castToNum(Long.class))
			.otherwise(0L);

		// 만료예정
		NumberExpression<Long> expectedExpiredCase = new CaseBuilder()
			.when(contract.contractEndDate.year().eq(year).and(contract.contractEndDate.month().eq(month)))
			.then(Expressions.asNumber(1).castToNum(Long.class))
			.otherwise(0L);

		return queryFactory
			.select(new QContractStatQueryRes(
				contract.id.count(),
				newContractCase.sum(),
				expectedExpiredCase.sum()))
			.from(contract)
			.where(
				contract.vendor.id.eq(vendorId),
				contract.deleted.isFalse())
			.fetchOne();
	}

	/**
	 * 청구
	 * 전체 금액, 완납, 미납
	 * */
	public BillingStatQueryRes findBillingStat(Long vendorId) {
		// 전체금액
		NumberExpression<Long> priceCase =
			billingProduct.price.longValue().multiply(billingProduct.quantity);

		// 완납
		NumberExpression<Long> paidBillingCase = new CaseBuilder()
			.when(billing.billingStatus.eq(BillingStatus.PAID))
			.then(Expressions.asNumber(1).castToNum(Long.class))
			.otherwise(0L);

		// 미납
		NumberExpression<Long> nonPaidBillingCase = new CaseBuilder()
			.when(billing.billingStatus.eq(BillingStatus.NON_PAID))
			.then(Expressions.asNumber(1).castToNum(Long.class))
			.otherwise(0L);

		return queryFactory
			.select(new QBillingStatQueryRes(
				priceCase.sum(),
				priceCase.multiply(paidBillingCase).sum(),
				priceCase.multiply(nonPaidBillingCase).sum())
			)
			.from(billingProduct)
			.join(billingProduct.billing, billing)
			.join(billing.contract, contract)
			.where(
				billing.contract.vendor.id.eq(vendorId),
				billingProduct.deleted.isFalse()
			)
			.fetchOne();
	}

	/**
	 * 특정 달 까지 등록된 회원 수
	 * */
	public Long countMemberEnrollmentsByMonth(Long vendorId, int year, int month) {
		LocalDate endOfMonth = LocalDate.of(year, month, 1)
			.plusMonths(1)
			.minusDays(1);

		// 달까지 등록된 회원수
		Long res = queryFactory
			.select(member.count())
			.from(member)
			.where(
				member.vendor.id.eq(vendorId),
				member.enrollDate.before(endOfMonth),
				member.deleted.isFalse())
			.fetchOne();

		return (res == null) ? 0 : res;
	}

	/**
	 * 특정 달의 청구 총액
	 * */
	public Long calcBillingPriceByMonth(Long vendorId, int month, int year) {
		LocalDate endOfCurrentMonth = LocalDate.of(year, month, 1)
			.plusMonths(1)
			.minusDays(1);

		// 이번달 청구
		List<Long> currentMonthBillingIds = queryFactory
			.select(billing.id)
			.from(billing)
			.join(billing.contract, contract)
			.where(
				contract.vendor.id.eq(vendorId),
				billing.deleted.isFalse(),
				billing.billingDate.year().eq(year),
				billing.billingDate.month().eq(month)
			)
			.fetch();

		// 이번달까지의 매출
		Long currentMonthPrice = queryFactory
			.select(billingProduct.price.longValue().multiply(billingProduct.quantity).sum())
			.from(billingProduct)
			.where(
				billingProduct.billing.id.in(currentMonthBillingIds),
				billingProduct.deleted.isFalse()
			)
			.fetchOne();

		return (currentMonthPrice != null) ? currentMonthPrice : 0L;
	}

	/**
	 * 특정 달의 청구 요약 목록
	 * */
	public List<DayBillingQueryRes> findBillingsByMonth(Long vendorId, int year, int month) {
		return queryFactory
			.select(new QDayBillingQueryRes(
				billing.billingDate,
				billing.billingStatus,
				billing.id.countDistinct().intValue().nullif(0),
				billingProduct.price.longValue().multiply(billingProduct.quantity).sum().nullif(0L)))
			.from(billing)
			.join(billing.contract, contract)
			.leftJoin(billing.billingProducts, billingProduct).on(billingProduct.deleted.isFalse())
			.where(
				contract.vendor.id.eq(vendorId),
				billing.billingDate.month().eq(month),
				billing.billingDate.year().eq(year))
			.groupBy(billing.billingDate, billing.billingStatus, billing.id)
			.fetch();
	}

	/**
	 * 특정 날의 청구 상세 목록
	 * */
	public List<Billing> findBillingsByDay(Long vendorId, LocalDate date) {
		return queryFactory
			.selectFrom(billing)
			.leftJoin(billing.billingProducts, billingProduct)
			.on(billingProduct.deleted.isFalse()).fetchJoin()
			.join(billing.contract, contract)
			.where(
				contract.vendor.id.eq(vendorId),
				billing.billingDate.eq(date))
			.fetch();
	}

	/**
	 * 계약 금액 순 TOP 5 회원
	 * */
	public List<TopFiveMemberQueryRes> findTopFiveMembers(Long vendorId) {
		return queryFactory
			.select(new QTopFiveMemberQueryRes(
				member.id,
				member.name, contractProduct.price.longValue().multiply(contractProduct.quantity).sum(),
				contract.id.countDistinct().intValue()))
			.from(member)
			.leftJoin(member.contracts, contract)
			.leftJoin(contract.contractProducts, contractProduct).on(contractProduct.deleted.isFalse())
			.groupBy(member.id)
			.where(member.deleted.isFalse(), member.vendor.id.eq(vendorId))
			.orderBy(
				contractProduct.price.longValue().multiply(contractProduct.quantity).sum().desc(),
				contract.id.countDistinct().desc())
			.limit(5)
			.fetch();
	}

	/**
	 * 최신순 TOP 5 계약
	 * */
	public List<RecentFiveContractQueryRes> findRecentFiveContracts(Long vendorId) {
		return queryFactory
			.select(new QRecentFiveContractQueryRes(
				contract.id,
				contract.createdDateTime,
				member.name,
				contractProduct.price.longValue().multiply(contractProduct.quantity).sum(),
				contract.contractStartDate,
				contract.contractEndDate))
			.from(contract)
			.join(contract.member, member)
			.leftJoin(contract.contractProducts, contractProduct).on(contractProduct.deleted.isFalse())
			.where(contract.deleted.isFalse(), contract.vendor.id.eq(vendorId))
			.groupBy(contract.id)
			.orderBy(contract.createdDateTime.desc())
			.limit(5)
			.fetch();

	}
}
