package kr.or.kosa.cmsplusmain.domain.payment.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.or.kosa.cmsplusmain.domain.payment.entity.PaymentType;

@Converter(autoApply = true)
public class PaymentTypeConverter implements AttributeConverter<PaymentType, String> {

	@Override
	public String convertToDatabaseColumn(PaymentType attribute) {
		return (attribute == null) ? null : attribute.getName();
	}

	@Override
	public PaymentType convertToEntityAttribute(String dbData) {
		return PaymentType.fromName(dbData);
	}
}
