package kr.or.kosa.cmsplusmain.domain.payment.entity;

import java.time.LocalDate;

import lombok.*;
import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.validator.PersonName;
import kr.or.kosa.cmsplusmain.domain.payment.validator.CardNumber;

@Comment("결제수단 - 카드")
@Entity
@DiscriminatorValue(PaymentMethod.Values.CARD)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CardPayment extends PaymentMethodInfo {

	@Comment("카드 번호")
	@Column(name = "card_info_number", nullable = false)
	@CardNumber
	@NotNull
	private String cardNumber;

	@Comment("카드 소유주명")
	@Column(name = "card_info_owner", nullable = false)
	@PersonName
	@NotNull
	private String cardOwner;

	@Comment("카드 소유주 생년월일")
	@Column(name = "card_info_owner_birth", nullable = false)
	@NotNull
	private LocalDate cardOwnerBirth;
}

