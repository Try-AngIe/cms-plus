package kr.or.kosa.cmsplusmain.domain.base.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HomePhoneValidator implements ConstraintValidator<HomePhone, String> {

	private static final Pattern USERNAME_PATTERN = Pattern.compile(
		"^\\d{2,3}-?\\d{3,4}-?\\d{4}$"
	);

	@Override
	public void initialize(HomePhone constraintAnnotation) {
	}

	@Override
	public boolean isValid(String homePhone, ConstraintValidatorContext context) {
		if (homePhone == null) {
			return true; // null 값은 다른 어노테이션으로 처리
		}
		return USERNAME_PATTERN.matcher(homePhone).matches();
	}
}