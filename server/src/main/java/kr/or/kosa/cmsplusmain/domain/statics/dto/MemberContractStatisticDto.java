package kr.or.kosa.cmsplusmain.domain.statics.dto;

import com.querydsl.core.annotations.QueryProjection;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import lombok.Getter;

@Getter
public class MemberContractStatisticDto {
    private Long contractId;
    private String memberName;
    private Integer enroll_year;
    private Integer contract_duration;
    private Long total_contract_amount;
    private String payment_type;
    private Boolean renewed;

    @QueryProjection
    public MemberContractStatisticDto(Long contractId, String memberName, Integer enroll_year, Integer contract_duration, Long total_contract_amount, PaymentType payment_type, Boolean renewed) {
        this.contractId = contractId;
        this.memberName = memberName;
        this.enroll_year = enroll_year;
        this.contract_duration = contract_duration;
        this.total_contract_amount = total_contract_amount;
        this.payment_type = payment_type != null ? payment_type.getCode() : null;
        this.renewed = renewed;
    }
}