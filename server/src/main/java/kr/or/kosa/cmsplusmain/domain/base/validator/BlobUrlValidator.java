package kr.or.kosa.cmsplusmain.domain.base.validator;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BlobUrlValidator implements ConstraintValidator<BlobUrl, String> {

    private static final Pattern BLOB_URL_PATTERN = Pattern.compile(
            "^blob:https?://[^\\s/$.?#].[^\\s]*$"
    );

    @Override
    public void initialize(BlobUrl constraintAnnotation) {
    }

    @Override
    public boolean isValid(String blobUrl, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(blobUrl)) {
            return true; // null 값은 다른 어노테이션으로 처리
        }
        return BLOB_URL_PATTERN.matcher(blobUrl).matches();
    }
}