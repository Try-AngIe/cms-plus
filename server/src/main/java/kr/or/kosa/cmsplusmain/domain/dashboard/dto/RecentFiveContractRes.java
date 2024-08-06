package kr.or.kosa.cmsplusmain.domain.dashboard.dto;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class RecentFiveContractRes {
	private final Long contractId;
	private final LocalDate contractCreatedDate;
	private final String memberName;
	private final Long totalContractPrice;
	private final int contractMonth;

	public RecentFiveContractRes(Long contractId, LocalDate contractCreatedDate, String memberName,
		Long totalContractPrice, int contractMonth) {
		this.contractId = contractId;
		this.contractCreatedDate = contractCreatedDate;
		this.memberName = memberName;
		this.totalContractPrice = totalContractPrice;
		this.contractMonth = contractMonth;
	}
}
