package kr.or.kosa.cmsplusmain.domain.base.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HttpUrlValidator implements ConstraintValidator<HttpUrl, String> {

	private static final Pattern USERNAME_PATTERN = Pattern.compile(
		"^\\d{2,3}-?\\d{3,4}-?\\d{4}$"
	);

	@Override
	public void initialize(HttpUrl constraintAnnotation) {
	}

	@Override
	public boolean isValid(String httpUrl, ConstraintValidatorContext context) {
		if (httpUrl == null) {
			return true; // null 값은 다른 어노테이션으로 처리
		}
		return USERNAME_PATTERN.matcher(httpUrl).matches();
	}
}