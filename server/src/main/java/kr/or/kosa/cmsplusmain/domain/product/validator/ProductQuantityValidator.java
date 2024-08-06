package kr.or.kosa.cmsplusmain.domain.product.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductQuantityValidator implements ConstraintValidator<ProductQuantity, Integer> {

    public static final int MAX_PRODUCT_QUANTITY = 10; // 1억

    @Override
    public void initialize(ProductQuantity constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer productQuantity, ConstraintValidatorContext context) {
        if (productQuantity == null) {
            return true;
        }
        return productQuantity < MAX_PRODUCT_QUANTITY;
    }
}
