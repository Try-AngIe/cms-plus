package kr.or.kosa.cmsplusmain.domain.payment.dto;

import kr.or.kosa.cmsplusmain.domain.payment.entity.AutoPayment;
import kr.or.kosa.cmsplusmain.domain.payment.entity.CardPayment;
import kr.or.kosa.cmsplusmain.domain.payment.entity.CmsPayment;
import kr.or.kosa.cmsplusmain.domain.payment.entity.ConsentStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusmain.domain.payment.entity.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.PaymentMethodInfo;
import kr.or.kosa.cmsplusmain.domain.payment.entity.PaymentStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.PaymentType;
import kr.or.kosa.cmsplusmain.domain.payment.entity.VirtualAccountPayment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentDto {
	private Long paymentId;
	private PaymentType paymentType;
	private PaymentStatus paymentStatus;
	private ConsentStatus consentStatus;

	// 자동결제
	private CardInfo cardInfo;
	private CMSInfo cmsInfo;

	// 가상계좌
	private VirtualAccountInfo virtualAccountInfo;

	public static PaymentDto fromEntity(Payment payment) {
		PaymentType paymentType = payment.getPaymentType();

		PaymentDto.PaymentDtoBuilder paymentDtoBuilder = PaymentDto.builder()
			.paymentId(payment.getId())
			.paymentType(paymentType)
			.paymentStatus(payment.getStatus())
			.consentStatus(payment.getConsentStatus());

		switch (paymentType) {
			case AUTO -> {
				AutoPayment autoPayment = (AutoPayment)payment;

				// NULLABLE
				// 신규 회원 등록시 결제수단 정보를 회원설정으로 설정시 NULL
				PaymentMethodInfo paymentMethodInfo = autoPayment.getPaymentMethodInfo();
				if (paymentMethodInfo == null) {
					break;
				}

				PaymentMethod paymentMethod = paymentMethodInfo.getPaymentMethod();
				switch (paymentMethod) {
					case CMS -> {
						CmsPayment cmsPayment = (CmsPayment)paymentMethodInfo;
						CMSInfo cmsInfo = CMSInfo.builder()
							.bank(cmsPayment.getBank())
							.accountNumber(cmsPayment.getAccountNumber())
							.accountOwner(cmsPayment.getAccountOwner())
							.accountOwnerBirth(cmsPayment.getAccountOwnerBirth())
							.build();
						paymentDtoBuilder = paymentDtoBuilder.cmsInfo(cmsInfo);
					}
					case CARD -> {
						CardPayment cardPayment = (CardPayment)paymentMethodInfo;
						CardInfo cardInfo = CardInfo.builder()
							.cardNumber(cardPayment.getCardNumber())
							.cardOwner(cardPayment.getCardOwner())
							.cardOwnerBirth(cardPayment.getCardOwnerBirth())
							.build();
						paymentDtoBuilder = paymentDtoBuilder.cardInfo(cardInfo);
					}
				}
			}
			case VIRTUAL -> {
				VirtualAccountPayment virtualAccountPayment = (VirtualAccountPayment)payment;
				VirtualAccountInfo virtualAccountInfo = VirtualAccountInfo.builder()
					.bank(virtualAccountPayment.getBank())
					.accountOwner(virtualAccountPayment.getAccountOwner())
					.accountNumber(virtualAccountPayment.getAccountNumber())
					.build();
				paymentDtoBuilder = paymentDtoBuilder.virtualAccountInfo(virtualAccountInfo);
			}
		}

		return paymentDtoBuilder.build();
	}
}
