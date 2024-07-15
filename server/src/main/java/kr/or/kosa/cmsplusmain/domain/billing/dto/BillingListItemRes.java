package kr.or.kosa.cmsplusmain.domain.billing.dto;

import java.time.LocalDate;
import java.util.List;

import kr.or.kosa.cmsplusmain.domain.billing.entity.Billing;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingStandard;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingStatus;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusmain.domain.payment.entity.PaymentType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BillingListItemRes {
	private final Long billingId;                            	// 청구 ID
	private final String memberName;                        	// 회원명
	private final String memberPhone;                        	// 회원 휴대번호
	private final List<BillingProductRes> billingProducts;    	// 청구 상품 목록
	private final Long billingPrice;                        	// 청구금액
	private final BillingStatus billingStatus;                	// 청구상태
	private final PaymentType paymentType;                    	// 결제방식
	private final LocalDate billingDate;                    	// 청구의 결제일

	public static BillingListItemRes fromEntity(Billing billing) {

		// NOT NULL
		final BillingStandard billingStandard = billing.getBillingStandard();
		final Contract contract = billingStandard.getContract();
		final Member member = contract.getMember();

		final List<BillingProductRes> billingProductResList = billingStandard.getBillingProducts()
			.stream()
			.map(BillingProductRes::fromEntity)
			.toList();

		final Payment payment = contract.getPayment();

		return BillingListItemRes.builder()
			.billingId(billing.getId())
			.memberName(member.getName())
			.memberPhone(member.getPhone())
			.billingProducts(billingProductResList)
			.billingPrice(billingStandard.getBillingPrice())
			.billingStatus(billing.getBillingStatus())
			.paymentType(payment.getPaymentType())
			.billingDate(billing.getBillingDate())
			.build();
	}
}
