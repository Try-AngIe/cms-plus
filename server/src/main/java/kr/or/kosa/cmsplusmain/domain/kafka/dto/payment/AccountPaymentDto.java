package kr.or.kosa.cmsplusmain.domain.kafka.dto.payment;

import kr.or.kosa.cmsplusmain.domain.kafka.PaymentPayMethod;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountPaymentDto extends PaymentDto {

    public AccountPaymentDto(Long billingId, String phoneNumber, String number) {
        super(billingId, phoneNumber, PaymentPayMethod.ACCOUNT, number);
    }

}
