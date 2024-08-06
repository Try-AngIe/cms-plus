package kr.or.kosa.cmsplusmain.domain.dashboard.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingStatus;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.DayBillingRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.MonthBillingInfoRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.RecentFiveContractRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.StatInfoRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.TopFiveMemberRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.TopInfoRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.BillingStatQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.ContractStatQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.DayBillingQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.MemberStatQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.RecentFiveContractQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.query.TopFiveMemberQueryRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.repository.StatRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatService {

	private final StatRepository statRepository;

	public StatInfoRes getStatInfo(Long vendorId, int year, int month) {
		MemberStatQueryRes memberStat = statRepository.findMemberStat(vendorId, year, month);
		ContractStatQueryRes contractStat = statRepository.findContractStat(vendorId, year, month);
		BillingStatQueryRes billingStat = statRepository.findBillingStat(vendorId);

		Double memberGrowth = getMemberGrowth(vendorId, year, month);
		Double billingPriceGrowth = getBillingPriceGrowth(vendorId, year, month);

		return new StatInfoRes(
			billingStat.getTotalBillingPrice(),
			billingStat.getTotalPaidPrice(),
			billingStat.getTotalNotPaidPrice(),
			contractStat.getTotalContractCount(),
			contractStat.getNewContractCount(),
			contractStat.getExpectedToExpiredCount(),
			memberStat.getTotalMemberCount(),
			memberStat.getNewMemberCount(),
			memberStat.getActiveMemberCount(),
			memberGrowth, billingPriceGrowth);
	}

	private Double getMemberGrowth(Long vendorId, int year, int month) {
		int lastMonth;
		int lastYear;

		if (month == 1) {
			lastYear = year - 1;
			lastMonth = 12;
		} else {
			lastYear = year;
			lastMonth = month - 1;
		}

		Long curMonthMemberCount = statRepository.countMemberEnrollmentsByMonth(vendorId, year, month);
		Long prevMonthMemberCount = statRepository.countMemberEnrollmentsByMonth(vendorId, lastYear, lastMonth);

		if (prevMonthMemberCount == 0) {
			return 0.0;
		}
		return ((double)(curMonthMemberCount - prevMonthMemberCount) / prevMonthMemberCount) * 100;
	}

	private Double getBillingPriceGrowth(Long vendorId, int year, int month) {
		int lastMonth;
		int lastYear;

		if (month == 1) {
			lastYear = year - 1;
			lastMonth = 12;
		} else {
			lastYear = year;
			lastMonth = month - 1;
		}

		Long curMonthBillingPrice = statRepository.calcBillingPriceByMonth(vendorId, month, year);
		Long prevMonthBillingPrice = statRepository.calcBillingPriceByMonth(vendorId, lastMonth, lastYear);

		if (prevMonthBillingPrice == 0) {
			return 100.0;
		}
		return ((double)(curMonthBillingPrice - prevMonthBillingPrice) / prevMonthBillingPrice) * 100;
	}

	public TopInfoRes getTopInfo(Long vendorId) {
		return new TopInfoRes(
			getTopFiveMember(vendorId),
			getRecentFiveContracts(vendorId)
		);
	}

	private List<TopFiveMemberRes> getTopFiveMember(Long vendorId) {
		List<TopFiveMemberQueryRes> queryRes = statRepository.findTopFiveMembers(vendorId);
		return queryRes.stream()
			.map(res -> new TopFiveMemberRes(res.getMemberId(), res.getMemberName(), res.getTotalContractPrice(),
				res.getContractCount()))
			.toList();
	}

	private List<RecentFiveContractRes> getRecentFiveContracts(Long vendorId) {
		List<RecentFiveContractQueryRes> queryRes = statRepository.findRecentFiveContracts(vendorId);
		return queryRes.stream()
			.map(res -> new RecentFiveContractRes(
				res.getContractId(),
				res.getCreateDateTime().toLocalDate(),
				res.getMemberName(), res.getTotalContractPrice(),
				Period.between(res.getContractStartDate(), res.getContractEndDate()).getMonths()))
			.toList();
	}

	public MonthBillingInfoRes getMonthBillingInfo(Long vendorId, int year, int month) {
		List<DayBillingQueryRes> dayBillingQueryRes = statRepository.findBillingsByMonth(vendorId, year, month);

		int totalBillingAmount = dayBillingQueryRes.stream()
			.mapToInt(DayBillingQueryRes::getBillingCount)
			.sum();
		Map<BillingStatus, List<DayBillingQueryRes>> statusToQRes = groupByStatus(dayBillingQueryRes);
		Map<BillingStatus, Long> statusToPrices = calculateStatusPrices(statusToQRes);
		Map<BillingStatus, Integer> statusToCounts = calculateStatusCounts(statusToQRes);
		Long totalBillingPrice = calculateTotalBillingPrice(statusToPrices);
		List<DayBillingRes> dayBillingRes = createDayBillingResList(dayBillingQueryRes);

		return MonthBillingInfoRes.builder()
			.statusPrices(statusToPrices)
			.statusCounts(statusToCounts)
			.dayBillingRes(dayBillingRes)
			.totalBillingPrice(totalBillingPrice)
			.totalBillingAmount(totalBillingAmount)
			.build();
	}

	private Map<BillingStatus, List<DayBillingQueryRes>> groupByStatus(List<DayBillingQueryRes> dayBillingQueryRes) {
		return dayBillingQueryRes.stream()
			.collect(Collectors.groupingBy(DayBillingQueryRes::getBillingStatus));
	}

	private Map<BillingStatus, Long> calculateStatusPrices(Map<BillingStatus, List<DayBillingQueryRes>> statusToQRes) {
		return statusToQRes.entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> entry.getValue().stream()
					.mapToLong(DayBillingQueryRes::getBillingPrice)
					.sum()
			));
	}

	private Map<BillingStatus, Integer> calculateStatusCounts(
		Map<BillingStatus, List<DayBillingQueryRes>> statusToQRes) {
		return statusToQRes.entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> entry.getValue().size()
			));
	}

	private Long calculateTotalBillingPrice(Map<BillingStatus, Long> statusToPrices) {
		return statusToPrices.values().stream()
			.mapToLong(Long::longValue)
			.sum();
	}

	private List<DayBillingRes> createDayBillingResList(List<DayBillingQueryRes> dayBillingQueryRes) {
		return dayBillingQueryRes.stream()
			.collect(Collectors.groupingBy(DayBillingQueryRes::getBillingDate))
			.entrySet().stream()
			.map(entry -> createDayBillingRes(entry.getKey(), entry.getValue()))
			.toList();
	}

	private DayBillingRes createDayBillingRes(LocalDate day, List<DayBillingQueryRes> qResList) {
		long totalDayBillingPrice = qResList.stream()
			.mapToLong(DayBillingQueryRes::getBillingPrice)
			.sum();

		Map<BillingStatus, Integer> statusCounts = qResList.stream()
			.collect(Collectors.groupingBy(
				DayBillingQueryRes::getBillingStatus,
				Collectors.summingInt(e -> 1)
			));

		return DayBillingRes.builder()
			.billingDate(day)
			.totalDayBillingCount(qResList.size())
			.totalDayBillingPrice(totalDayBillingPrice)
			.statusCounts(statusCounts)
			.build();
	}
}
