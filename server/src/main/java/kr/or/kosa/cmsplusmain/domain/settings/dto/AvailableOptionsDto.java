package kr.or.kosa.cmsplusmain.domain.settings.dto;

import kr.or.kosa.cmsplusmain.domain.payment.entity.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.product.dto.ProductRes;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class AvailableOptionsDto {
    private Set<PaymentMethod> availablePaymentMethods;
    private List<ProductRes> availableProducts;

    public AvailableOptionsDto(Set<PaymentMethod> availablePaymentMethods, List<ProductRes> availableProducts) {
        this.availablePaymentMethods = availablePaymentMethods;
        this.availableProducts = availableProducts;
    }
}
