package kr.or.kosa.cmsplusmain.domain.payment.dto;

import kr.or.kosa.cmsplusmain.domain.base.validator.PersonName;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Bank;
import kr.or.kosa.cmsplusmain.domain.payment.validator.AccountNumber;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VirtualAccountInfo {

	private Bank bank;

	@PersonName
	private String accountOwner;

	@AccountNumber
	private String accountNumber;
}
