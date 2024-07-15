package kr.or.kosa.cmsplusmain.domain.billing.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InvoiceMessageValidator implements ConstraintValidator<InvoiceMessage, String> {
	private static final int MAX_MESSAGE_LENGTH = 2000;

	@Override
	public boolean isValid(String homePhone, ConstraintValidatorContext context) {
		if (homePhone == null) {
			return true; // null 값은 다른 어노테이션으로 처리
		}
		return homePhone.length() < MAX_MESSAGE_LENGTH;
	}
}