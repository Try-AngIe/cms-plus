package kr.or.kosa.cmsplusmain.domain.payment.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import kr.or.kosa.cmsplusmain.domain.base.validator.HttpUrl;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Comment("결제방식 - 자동결제")
@Entity
@DiscriminatorValue(PaymentType.Values.AUTO)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutoPayment extends Payment {

	/*
	 * 회원설정으로 회원 등록한 경우 null
	 * */
	@Comment("결제수단 세부정보")
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_method_info_id")
	@NotFound(action = NotFoundAction.IGNORE)
	private PaymentMethodInfo paymentMethodInfo;

	@Comment("회원 간편동의 서명 이미지 URL")
	@Column(name = "payment_sign_img_url", length = 2000)
	@HttpUrl
	private String signImgUrl;

	@Comment("고객이 등록한 동의서 이미지 URL")
	@Column(name = "payment_consent_img_url", length = 2000)
	@HttpUrl
	private String consentImgUrl;

	@Comment("간편동의 마지막 요청시간")
	@Column(name = "payment_simpconsent_request_date")
	private LocalDateTime simpleConsentReqDateTime;

	/*
	 * 결제정보 상태
	 *
	 * 동의가 필요하다.
	 * */
	@Override
	public PaymentStatus getPaymentStatus() {
		return (getConsentStatus().equals(ConsentStatus.ACCEPT)) ?
			PaymentStatus.ENABLED : PaymentStatus.DISABLED;
	}

	/*
	 * 동의상태
	 *
	 * "동의" -> 간편동의 서명 이미지 or 고객이 등록한 동의서 이미지 중 1개 존재
	 * "대기중" -> 두 이미지 파일 없음 and 간편동의 마지막 요청시간이 존재
	 * "미동의" -> 이미지 두 개 모두 없고, 요청도 한 적이 없음
	 * */
	@Override
	public ConsentStatus getConsentStatus() {
		if (!(StringUtils.isBlank(signImgUrl) && StringUtils.isBlank(consentImgUrl))) {
			return ConsentStatus.ACCEPT;
		} else if (simpleConsentReqDateTime != null) {
			return ConsentStatus.WAIT;
		} else {
			return ConsentStatus.NONE;
		}
	}

	/*
	 * 간편동의 발송
	 *
	 * 최종 발송 시각을 현재 시각으로 설정
	 * */
	public void sendSimpConsent() {
		this.simpleConsentReqDateTime = LocalDateTime.now();
	}
}
