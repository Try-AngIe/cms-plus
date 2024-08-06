package kr.or.kosa.cmsplusmain.domain.base.validator;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<Username, String> {

	private static final Pattern USERNAME_PATTERN = Pattern.compile(
		"^[a-z0-9]{5,20}$"
	);

	@Override
	public void initialize(Username constraintAnnotation) {
	}

	@Override
	public boolean isValid(String username, ConstraintValidatorContext context) {
		if (!StringUtils.hasText(username)) {
			return true; // null 값은 다른 어노테이션으로 처리
		}
		return USERNAME_PATTERN.matcher(username).matches();
	}
}