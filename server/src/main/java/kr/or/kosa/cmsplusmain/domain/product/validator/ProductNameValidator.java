package kr.or.kosa.cmsplusmain.domain.product.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductNameValidator implements ConstraintValidator<ProductName, String> {

	private static final int MAX_NAME_LENGTH = 20;
	private static final int MIN_NAME_LENGTH = 1;

	@Override
	public void initialize(ProductName constraintAnnotation) {
	}

	@Override
	public boolean isValid(String productName, ConstraintValidatorContext context) {
		if (productName == null) {
			return true; // null 값은 다른 어노테이션으로 처리
		}
		return productName.length() >= MIN_NAME_LENGTH && productName.length() <= MAX_NAME_LENGTH;
	}
}