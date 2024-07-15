package kr.or.kosa.cmsplusmain.domain.vendor.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

	private static final Pattern PASSWORD_PATTERN = Pattern.compile(
		"^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+<>?]).{8,16}$"
	);

	@Override
	public void initialize(Password constraintAnnotation) {
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		if (password == null) {
			return true; // null 값은 다른 어노테이션으로 처리
		}
		return PASSWORD_PATTERN.matcher(password).matches();
	}
}