package kr.or.kosa.cmsplusmain.domain.contract.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContractNameValidator implements ConstraintValidator<ContractName, String> {

	private static final Pattern NAME_PATTERN = Pattern.compile(
		"^[가-힣a-zA-Z]{1,40}$"
	);

	@Override
	public boolean isValid(String contractName, ConstraintValidatorContext context) {
		if (contractName == null) {
			return true; // null 값은 다른 어노테이션으로 처리
		}
		return NAME_PATTERN.matcher(contractName).matches();
	}
}