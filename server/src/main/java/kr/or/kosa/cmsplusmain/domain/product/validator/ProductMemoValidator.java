package kr.or.kosa.cmsplusmain.domain.product.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductMemoValidator implements ConstraintValidator<ProductMemo, String> {

	private static final int MAX_MEMO_LENGTH = 2000;

	@Override
	public void initialize(ProductMemo constraintAnnotation) {
	}

	@Override
	public boolean isValid(String productMemo, ConstraintValidatorContext context) {
		if (productMemo == null) {
			return true; // null 값은 다른 어노테이션으로 처리
		}
		return productMemo.length() <= MAX_MEMO_LENGTH;
	}
}