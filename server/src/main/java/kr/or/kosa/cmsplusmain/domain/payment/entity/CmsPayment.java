package kr.or.kosa.cmsplusmain.domain.payment.entity;

import java.time.LocalDate;

import kr.or.kosa.cmsplusmain.domain.payment.dto.CMSInfo;
import lombok.*;
import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.validator.PersonName;
import kr.or.kosa.cmsplusmain.domain.payment.converter.BankConverter;
import kr.or.kosa.cmsplusmain.domain.payment.validator.AccountNumber;

@Comment("결제수단 - CMS")
@Entity
@DiscriminatorValue(PaymentMethod.Values.CMS)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CmsPayment extends PaymentMethodInfo {

	@Comment("CMS 계좌 은행")
	@Column(name = "cms_account_bank", nullable = false)
	@Convert(converter = BankConverter.class)
	private Bank bank;

	@Comment("CMS 계좌번호")
	@Column(name = "cms_account_number", nullable = false, length = 15)
	@AccountNumber
	@NotNull
	private String accountNumber;

	@Comment("CMS 계좌 소유주명")
	@Column(name = "cms_account_owner", nullable = false, length = 40)
	@PersonName
	@NotNull
	private String accountOwner;

	@Comment("CMS 계좌 소유주 생년월일")
	@Column(name = "cms_owner_birth", nullable = false)
	@NotNull
	private LocalDate accountOwnerBirth;

}

