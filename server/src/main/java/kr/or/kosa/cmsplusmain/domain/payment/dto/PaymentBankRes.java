package kr.or.kosa.cmsplusmain.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentBankRes {
    private String code;
    private String title;
    private String name;
}
