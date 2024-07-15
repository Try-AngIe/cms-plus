package kr.or.kosa.cmsplusmain.domain.payment.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Bank;

@Converter(autoApply = true)
public class BankConverter implements AttributeConverter<Bank, String> {
	@Override
	public String convertToDatabaseColumn(Bank attribute) {
		return (attribute == null) ? null : attribute.getCode();
	}

	@Override
	public Bank convertToEntityAttribute(String dbData) {
		return Bank.fromCode(dbData);
	}
}
