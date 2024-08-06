package kr.or.kosa.cmsplusmain.domain.statics.dto;

import java.time.LocalDate;

import com.querydsl.core.annotations.QueryProjection;

import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import lombok.Getter;

@Getter
public class MemberContractStatisticRes {
    private String new_member_renewal_probability;
}