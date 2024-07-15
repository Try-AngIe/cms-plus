package kr.or.kosa.cmsplusmain.domain.base.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

	private static final Pattern PHONE_PATTERN = Pattern.compile(
		"^01(?:0|1|[6-9])-?(?:\\d{3}|\\d{4})-?\\d{4}$"
	);

	@Override
	public void initialize(Phone constraintAnnotation) {
	}

	@Override
	public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
		if (phoneNumber == null) {
			return true; // null 값은 다른 어노테이션으로 처리
		}
		return PHONE_PATTERN.matcher(phoneNumber).matches();
	}
}