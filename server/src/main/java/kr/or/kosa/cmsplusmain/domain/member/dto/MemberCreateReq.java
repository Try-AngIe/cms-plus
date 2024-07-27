package kr.or.kosa.cmsplusmain.domain.member.dto;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.validator.HomePhone;
import kr.or.kosa.cmsplusmain.domain.base.validator.Memo;
import kr.or.kosa.cmsplusmain.domain.base.validator.PersonName;
import kr.or.kosa.cmsplusmain.domain.base.validator.Phone;
import kr.or.kosa.cmsplusmain.domain.contract.dto.ContractCreateReq;
import kr.or.kosa.cmsplusmain.domain.kafka.MessageSendMethod;
import kr.or.kosa.cmsplusmain.domain.member.entity.Address;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.member.entity.MemberStatus;
import kr.or.kosa.cmsplusmain.domain.payment.dto.PaymentCreateReq;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberCreateReq {

    @NotNull
    @PersonName
    private String memberName; // 회원 이름

    @NotNull
    @Phone
    private String memberPhone; // 회원 휴대전화 번호

    @NotNull
    private LocalDate memberEnrollDate; // 회원 등록 날짜

    @HomePhone
    private String memberHomePhone; // 회원 유선전화 번호

    @NotNull
    @Email
    private String memberEmail; // 회원 이메일

    private Address memberAddress; // 회원 주소

    @Memo
    private String memberMemo; // 회원 메모

    @NotNull
    private MessageSendMethod invoiceSendMethod; // 청구 전송 방법

    @NotNull
    private boolean autoInvoiceSend; // 자동 청구서 발송

    @NotNull
    private boolean autoBilling; // 자동 청구 발송

    @Valid
    private PaymentCreateReq paymentCreateReq;   // 결제정보

    @Valid
    private ContractCreateReq contractCreateReq; // 계약정보

    public Member toEntity(Long vendorId) {
        return Member.builder()
                .vendor(Vendor.of(vendorId))
                .status(MemberStatus.DISABLED)
                .name(memberName)
                .email(memberEmail)
                .phone(memberPhone)
                .homePhone(memberHomePhone)
                .address(memberAddress)
                .memo(memberMemo)
                .enrollDate(memberEnrollDate)
                .invoiceSendMethod(invoiceSendMethod)
                .autoInvoiceSend(autoInvoiceSend)
                .autoBilling(autoBilling)
                .build();
    }
}
