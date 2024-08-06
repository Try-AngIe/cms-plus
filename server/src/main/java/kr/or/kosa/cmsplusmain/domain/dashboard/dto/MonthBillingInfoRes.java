package kr.or.kosa.cmsplusmain.domain.dashboard.dto;

import java.util.List;
import java.util.Map;

import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MonthBillingInfoRes {
	private final Long totalBillingPrice;
	private final Map<BillingStatus, Long> statusPrices;
	private final Integer totalBillingAmount;
	private final Map<BillingStatus, Integer> statusCounts;
	private final List<DayBillingRes> dayBillingRes;
}
