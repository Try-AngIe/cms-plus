package kr.or.kosa.cmsplusmain.domain.base.validator;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HomePhoneValidator implements ConstraintValidator<HomePhone, String> {

	private static final Pattern USERNAME_PATTERN = Pattern.compile(
		"^(02|0[3-6]{1}[1-5]{1})([0-9]{3,4})[0-9]{4}$"
	);

	@Override
	public void initialize(HomePhone constraintAnnotation) {
	}

	@Override
	public boolean isValid(String homePhone, ConstraintValidatorContext context) {
		if (!StringUtils.hasText(homePhone)) {
			return true; // null 값은 다른 어노테이션으로 처리
		}
		return USERNAME_PATTERN.matcher(homePhone).matches();
	}
}