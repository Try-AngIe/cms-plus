package kr.or.kosa.cmsplusmain.domain.base.validator;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PersonNameValidator implements ConstraintValidator<PersonName, String> {

	private static final Pattern NAME_PATTERN = Pattern.compile(
		"^[가-힣a-zA-Z0-9]{1,40}$"
	);

	@Override
	public boolean isValid(String personName, ConstraintValidatorContext context) {
		if (!StringUtils.hasText(personName)) {
			return true; // null 값은 다른 어노테이션으로 처리
		}
		return NAME_PATTERN.matcher(personName).matches();
	}
}