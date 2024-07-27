package kr.or.kosa.cmsplusmain.domain.payment.dto;

import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.payment.dto.method.PaymentMethodInfoReq;
import kr.or.kosa.cmsplusmain.domain.payment.dto.type.PaymentTypeInfoReq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateReq {
    // 결제수단 info req
    @NotNull
    private PaymentTypeInfoReq paymentTypeInfoReq;
    private PaymentMethodInfoReq paymentMethodInfoReq;
}
