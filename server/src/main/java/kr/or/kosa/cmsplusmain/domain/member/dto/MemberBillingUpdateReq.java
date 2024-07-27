package kr.or.kosa.cmsplusmain.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.kafka.MessageSendMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberBillingUpdateReq {
    @NotNull
    private MessageSendMethod invoiceSendMethod; // 청구 전송 방법

    @NotNull
    private boolean autoInvoiceSend; // 자동 청구서 발송

    @NotNull
    private boolean autoBilling; // 자동 청구 발송
}