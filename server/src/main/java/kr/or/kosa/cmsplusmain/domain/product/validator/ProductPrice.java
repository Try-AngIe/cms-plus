package kr.or.kosa.cmsplusmain.domain.product.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProductPriceValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProductPrice {

    String message() default "Invalid product price";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
