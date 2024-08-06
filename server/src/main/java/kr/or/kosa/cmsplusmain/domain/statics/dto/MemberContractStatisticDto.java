package kr.or.kosa.cmsplusmain.domain.statics.dto;

import com.querydsl.core.annotations.QueryProjection;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MemberContractStatisticDto {
    private Long contractId;
    private String memberName;
    private Integer enrollYear;
    private Integer contractDuration;
    private Long totalContractAmount;
    private String paymentType;
    private Boolean renewed;

    @QueryProjection
    public MemberContractStatisticDto(Long contractId, String memberName, Integer enrollYear, Integer contractDuration, Long totalContractAmount, PaymentType paymentType, Boolean renewed) {
        this.contractId = contractId;
        this.memberName = memberName;
        this.enrollYear = enrollYear;
        this.contractDuration = contractDuration;
        this.totalContractAmount = totalContractAmount;
        this.paymentType = paymentType != null ? paymentType.getCode() : null;
        this.renewed = renewed;
    }
}