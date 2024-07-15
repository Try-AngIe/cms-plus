package kr.or.kosa.cmsplusmain.domain.settings.dto;

import kr.or.kosa.cmsplusmain.domain.payment.entity.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.settings.entity.SimpConsentSetting;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class SimpConsentSettingDto {
    private Long id;
    private String vendorUsername;
    private Set<PaymentMethod> paymentMethods;
    private Set<Long> productIds;

}