package kr.or.kosa.cmsplusmain.domain.payment.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Comment;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Comment("결제방식-납부자결제")
@Entity
@DiscriminatorValue(PaymentType.Values.BUYER)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BuyerPayment extends Payment {

	@Comment("납부자결제 - 설정된 가능 결제수단")
	@ElementCollection(targetClass = PaymentMethod.class, fetch = FetchType.LAZY)
	@CollectionTable(name = "buyer_payment_method")
	@Enumerated(EnumType.STRING)
	@Column(name = "buyer_payment_method")
	private Set<PaymentMethod> availableMethods = new HashSet<>();

	/*
	 * 납부자결제 수단은 카드와 계좌만 가능하다.
	 * */
	public void setAvailableMethods(Set<PaymentMethod> availableMethods) {
		List<PaymentMethod> paymentMethods = PaymentType.BUYER.getAvailablePaymentMethods();
		if (availableMethods.stream().anyMatch(paymentMethods::contains)) {
			throw new IllegalArgumentException("납부자결제 가능 결제수단이 아닙니다.");
		}
	}
}
