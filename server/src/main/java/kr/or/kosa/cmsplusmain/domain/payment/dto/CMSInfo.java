package kr.or.kosa.cmsplusmain.domain.payment.dto;

import java.time.LocalDate;

import kr.or.kosa.cmsplusmain.domain.base.validator.PersonName;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Bank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CMSInfo {
	private Bank bank;

	private String accountNumber;

	@PersonName
	private String accountOwner;

	private LocalDate accountOwnerBirth;
}
