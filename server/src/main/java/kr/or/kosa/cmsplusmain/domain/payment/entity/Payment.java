package kr.or.kosa.cmsplusmain.domain.payment.entity;

import jakarta.persistence.CascadeType;
import lombok.*;
import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.PaymentMethodInfo;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentTypeInfo;

@Comment("결제정보")
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

	@Id
	@Column(name = "payment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("결제방식")
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_type")
	private PaymentType paymentType;

	@Comment("결제방식 정보")
	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name = "payment_type_info_id")
	private PaymentTypeInfo paymentTypeInfo;

	@Comment("결제수단")
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method")
	private PaymentMethod paymentMethod;

	@Comment("결제수단 정보")
	@OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name = "payment_method_info_id")
	private PaymentMethodInfo paymentMethodInfo;

	/*
	* 실시간 결제 가능 여부
	* */
	public boolean canPayRealtime() {
		return paymentMethod != null && paymentMethod.getCanPayRealtime();
	}

	/**
	 * 간편동의 요청 가능 여부
	 * */
	public boolean canReqSimpConsent() {

		return paymentType.canReqSimpConsent();
	}

	/*
	* 결제 취소 가능 여부
	* */
	public boolean canCancel() {
		return paymentType == PaymentType.BUYER || (paymentMethod != null && paymentMethod.getCanCancel());
	}

	/*
	 * 결제 정보 수정
	 */
	public void update(PaymentType newPaymentType, PaymentTypeInfo newPaymentTypeInfo,
					   PaymentMethod newPaymentMethod, PaymentMethodInfo newPaymentMethodInfo) {
		this.paymentType = newPaymentType;
		this.paymentTypeInfo = newPaymentTypeInfo;
		this.paymentMethod = newPaymentMethod;
		this.paymentMethodInfo = newPaymentMethodInfo;
	}

	/*
	 * 결제 삭제
	 * 결제방식 정보도 같이 삭제된다
	 * 결제수단 정보도 같이 삭제된다
	 * */
	@Override
	public void delete() {
		super.delete();
		paymentTypeInfo.delete();
		if (paymentMethodInfo != null) {
			paymentMethodInfo.delete();
		}
	}
}