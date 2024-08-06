package kr.or.kosa.cmsplusmain.domain.dashboard.dto.query;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class RecentFiveContractQueryRes {
	private final Long contractId;
	private final LocalDateTime createDateTime;
	private final String memberName;
	private final long totalContractPrice;
	private final LocalDate contractStartDate;
	private final LocalDate contractEndDate;

	@QueryProjection
	public RecentFiveContractQueryRes(Long contractId, LocalDateTime createDateTime, String memberName,
		long totalContractPrice,
		LocalDate contractStartDate, LocalDate contractEndDate) {
		this.contractId = contractId;
		this.createDateTime = createDateTime;
		this.memberName = memberName;
		this.totalContractPrice = totalContractPrice;
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
	}
}
