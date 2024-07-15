package kr.or.kosa.cmsplusmain.domain.product.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductPriceValidator implements ConstraintValidator<ProductPrice, Integer> {

    private static final int MAX_PRODUCT_PRICE = 100000000; // 1억

    @Override
    public void initialize(ProductPrice constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer productPrice, ConstraintValidatorContext context) {
        return productPrice <= MAX_PRODUCT_PRICE;
    }
}
