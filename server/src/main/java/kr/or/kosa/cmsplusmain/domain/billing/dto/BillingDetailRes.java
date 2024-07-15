package kr.or.kosa.cmsplusmain.domain.billing.dto;

import java.time.LocalDate;
import java.util.List;

import kr.or.kosa.cmsplusmain.domain.billing.entity.Billing;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingStandard;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingType;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusmain.domain.payment.entity.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.PaymentType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BillingDetailRes {

	private final Long memberId;							// 회원 ID
	private final String memberName;						// 회원 이름
	private final String memberPhone;						// 회원 휴대전화

	private final Long contractId;							// 계약 ID
	private final PaymentType paymentType;					// 결제방식
	private final PaymentMethod paymentMethod;				// 결제수단

	private final Long billingId;							// 청구 ID
	private final String billingName;						// 청구서명
	private final BillingType billingType;					// 청구타입
	private final LocalDate billingCreatedDate;				// 청구 생성일
	private final LocalDate billingDate;					// 청구 결제일
	private final String billingMemo;						// 청구서 메시지

	private final List<BillingProductRes> billingProducts;	// 청구상품 목록
	private final Long billingPrice;						// 청구금액

	public static BillingDetailRes fromEntity(Billing billing) {
		final BillingStandard billingStandard = billing.getBillingStandard();
		final Contract contract = billingStandard.getContract();
		final Member member = contract.getMember();
		final Payment payment = contract.getPayment();

		final List<BillingProductRes> billingProductResList = billingStandard.getBillingProducts()
			.stream()
			.map(BillingProductRes::fromEntity)
			.toList();

		return BillingDetailRes.builder()
			.memberId(member.getId())
			.memberName(member.getName())
			.memberPhone(member.getPhone())

			.contractId(contract.getId())
			.paymentType(payment.getPaymentType())
			.paymentMethod(payment.getPaymentMethod())

			.billingId(billing.getId())
			.billingName(billing.getBillingName())
			.billingType(billingStandard.getType())
			.billingCreatedDate(billing.getCreatedDateTime().toLocalDate())
			.billingDate(billing.getBillingDate())
			.billingMemo(billing.getInvoiceMessage())

			.billingProducts(billingProductResList)
			.billingPrice(billingStandard.getBillingPrice())
			.build();
	}
}
