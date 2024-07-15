package kr.or.kosa.cmsplusmain.domain.payment.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = CardNumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CardNumber {
	String message() default "Invalid card number";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
