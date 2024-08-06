package kr.or.kosa.cmsplusmain.domain.contract.validator;

import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContractNameValidator implements ConstraintValidator<ContractName, String> {

	private static final int MAX_NAME_LENGTH = 32;
	private static final int MIN_NAME_LENGTH = 1;

	@Override
	public boolean isValid(String contractName, ConstraintValidatorContext context) {
		if (!StringUtils.hasText(contractName)) {
			return true; // null 값은 다른 어노테이션으로 처리
		}
		return MIN_NAME_LENGTH <= contractName.length() && contractName.length() <= MAX_NAME_LENGTH;
	}
}