package kr.or.kosa.cmsplusmain.domain.product.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductQuantityValidator implements ConstraintValidator<ProductQuantity, Integer> {

    private static final int MAX_PRODUCT_QUANTITY = 100000000; // 1억

    @Override
    public void initialize(ProductQuantity constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer productQuantity, ConstraintValidatorContext context) {
        return productQuantity <= MAX_PRODUCT_QUANTITY;
    }
}
