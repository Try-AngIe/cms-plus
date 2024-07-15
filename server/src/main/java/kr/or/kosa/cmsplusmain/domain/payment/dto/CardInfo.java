package kr.or.kosa.cmsplusmain.domain.payment.dto;

import java.time.LocalDate;

import kr.or.kosa.cmsplusmain.domain.base.validator.PersonName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardInfo {
	private String cardNumber;
	@PersonName
	private String cardOwner;
	private LocalDate cardOwnerBirth;
}
