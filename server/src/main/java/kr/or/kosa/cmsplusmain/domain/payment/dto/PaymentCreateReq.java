package kr.or.kosa.cmsplusmain.domain.payment.dto;

import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.payment.dto.method.PaymentMethodInfoReq;
import kr.or.kosa.cmsplusmain.domain.payment.dto.type.PaymentTypeInfoReq;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentCreateReq {
    // 결제수단 info req
    @NotNull
    private PaymentTypeInfoReq paymentTypeInfoReq;
    private PaymentMethodInfoReq paymentMethodInfoReq;
}
