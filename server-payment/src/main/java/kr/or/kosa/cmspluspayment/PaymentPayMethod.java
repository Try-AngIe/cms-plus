package kr.or.kosa.cmspluspayment;

import lombok.Getter;

@Getter
public enum PaymentPayMethod {
    // 납부자결제(카드/계좌) | 가상계좌
    CARD("CARD"), ACCOUNT("ACCOUNT"), VIRTUAL("VIRTUAL");

    private final String title;

    PaymentPayMethod(String title) {
        this.title = title;
    }

}
