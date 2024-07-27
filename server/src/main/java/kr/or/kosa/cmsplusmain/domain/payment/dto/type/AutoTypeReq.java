package kr.or.kosa.cmsplusmain.domain.payment.dto.type;

import java.time.LocalDateTime;

import kr.or.kosa.cmsplusmain.domain.payment.entity.type.AutoPaymentType;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutoTypeReq extends PaymentTypeInfoReq {
	private final String consentImgUrl;
	private final String signImgUrl;
	private final LocalDateTime simpleConsentReqDateTime;

	@Builder
	public AutoTypeReq(String consentImgUrl, String signImgUrl, LocalDateTime simpleConsentReqDateTime) {
		super(PaymentType.AUTO);
		this.consentImgUrl = consentImgUrl;
		this.signImgUrl = signImgUrl;
		this.simpleConsentReqDateTime = simpleConsentReqDateTime;
	}

	public AutoPaymentType toEntity() {
		return AutoPaymentType.builder()
				.consentImgUrl(this.consentImgUrl)
				.signImgUrl(this.signImgUrl)
				.simpleConsentReqDateTime(this.simpleConsentReqDateTime)
				.build();
	}
}