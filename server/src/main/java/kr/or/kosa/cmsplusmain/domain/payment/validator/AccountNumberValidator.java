package kr.or.kosa.cmsplusmain.domain.payment.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class AccountNumberValidator implements ConstraintValidator<AccountNumber, String> {

	private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile(
		"^\\d{10,14}$"
	);

	@Override
	public void initialize(AccountNumber constraintAnnotation) {
	}

	@Override
	public boolean isValid(String accountNumber, ConstraintValidatorContext context) {
		if (accountNumber == null) {
			return true; // null 값은 다른 어노테이션으로 처리
		}
		return ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches();
	}
}