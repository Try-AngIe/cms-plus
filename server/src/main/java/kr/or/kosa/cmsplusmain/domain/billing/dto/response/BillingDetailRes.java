package kr.or.kosa.cmsplusmain.domain.billing.dto.response;

import java.util.Collections;
import java.util.Map;

import kr.or.kosa.cmsplusmain.domain.billing.dto.BillingDto;
import kr.or.kosa.cmsplusmain.domain.billing.entity.Billing;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingState;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberDto;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BillingDetailRes {
	private final MemberDto member;
	private final BillingDto billing;
	private final Long contractId;                            // 계약 ID
	private final PaymentType paymentType;                    // 결제방식
	private final PaymentMethod paymentMethod;                // 결제수단
	private final Map<BillingState.Field, BillingState> fieldToState;        // 동작 별 가능 상태 및 이유

	public static BillingDetailRes fromEntity(Billing mBilling) {
		final Contract contract = mBilling.getContract();
		final Member mMember = contract.getMember();
		final Payment mPayment = contract.getPayment();

		return new BillingDetailRes(
			MemberDto.fromEntity(mMember),
			BillingDto.fromEntity(mBilling),
			contract.getId(),
			mPayment.getPaymentType(),
			mPayment.getPaymentMethod(),
			Collections.emptyMap());
	}

	/**
	 * 청구 상세 페이지
	 * 청구의 여러 버튼 별 가능 여부와 불가능하다면 그 이유를 담아서 보내준다.
	 * */
	public static BillingDetailRes fromEntity(Billing mBilling, Map<BillingState.Field, BillingState> fieldToState) {
		final Contract contract = mBilling.getContract();
		final Member mMember = contract.getMember();
		final Payment mPayment = contract.getPayment();

		return new BillingDetailRes(
			MemberDto.fromEntity(mMember),
			BillingDto.fromEntity(mBilling),
			contract.getId(),
			mPayment.getPaymentType(),
			mPayment.getPaymentMethod(),
			fieldToState);
	}
}
