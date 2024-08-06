package kr.or.kosa.cmsplusmain.domain.billing.dto.response;

import kr.or.kosa.cmsplusmain.domain.billing.entity.Billing;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberDto;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.payment.dto.type.PaymentTypeInfoRes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvoiceRes {
	private final Long billingId;
	private final String billingStatus;
	private final MemberDto member;
	private final String invoiceName;
	private final String invoiceMessage;
	private final Long billingPrice;
	private final PaymentTypeInfoRes paymentType;

	public static InvoiceRes fromEntity(Billing billing, Member member, PaymentTypeInfoRes paymentType) {
		return new InvoiceRes(
			billing.getId(), billing.getBillingStatus().getTitle(), MemberDto.fromEntity(member),
			billing.getInvoiceName(), billing.getInvoiceMessage(), billing.getBillingPrice(),
			paymentType);
	}
}
