package kr.or.kosa.cmsplusmain.domain.payment.entity;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusmain.domain.payment.converter.PaymentTypeConverter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Comment("결제정보")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "payment_type")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Payment extends BaseEntity {

	@Id
	@Column(name = "payment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("결제정보 상태")
	@Column(name = "payment_status", nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private PaymentStatus status = PaymentStatus.ENABLED;

	/* DiscriminatorColumn */
	@Comment("결제방식")
	@Convert(converter = PaymentTypeConverter.class)
	@Column(name = "payment_type", insertable = false, updatable = false)
	private PaymentType paymentType;

	/*
	 * 자동결제 외에는 동의가 사용되지 않으므로 동의상태 NOT_USED
	 * */
	@Comment("동의상태")
	@Column(name = "payment_consent_status", nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private ConsentStatus consentStatus = ConsentStatus.NOT_USED;

	/*
	 * 결제정보 상태
	 *
	 * 결제방식 별 사용가능 여부 판단 기준이 다를 수 있다.
	 *
	 * TODO: 저장되는 값과 일치해야함
	 * */
	public PaymentStatus getPaymentStatus() {
		return this.status;
	}

	public PaymentMethod getPaymentMethod() {
		if (this.paymentType != PaymentType.AUTO) {
			return null;
		}

		AutoPayment autoPayment = (AutoPayment) this;
		PaymentMethodInfo paymentMethodInfo = autoPayment.getPaymentMethodInfo();
		return (paymentMethodInfo == null) ? null : paymentMethodInfo.getPaymentMethod();
	}
}
