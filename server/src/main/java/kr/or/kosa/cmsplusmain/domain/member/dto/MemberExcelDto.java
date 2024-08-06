package kr.or.kosa.cmsplusmain.domain.member.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.validator.PersonName;
import kr.or.kosa.cmsplusmain.domain.base.validator.Phone;
import kr.or.kosa.cmsplusmain.domain.excel.ExcelColumn;
import kr.or.kosa.cmsplusmain.domain.member.entity.Address;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import kr.or.kosa.cmsplusmain.util.FormatUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberExcelDto {
	@ExcelColumn(headerName = "회원명")
	@PersonName
	@NotBlank(message = "회원명이 필요합니다")
	private String memberName;

	@ExcelColumn(headerName = "휴대전화")
	@Phone @NotBlank(message = "휴대전화가 필요합니다")
	private String memberPhone;

	@ExcelColumn(headerName = "우편번호")
	private String zipcode;

	@ExcelColumn(headerName = "주소")
	private String address;

	@ExcelColumn(headerName = "상세주소")
	private String addressDetail;

	@ExcelColumn(headerName = "등록일")
	@NotNull(message = "등록일이 필요합니다")
	private LocalDate memberEnrollDate;
	@ExcelColumn(headerName = "유선전화")
	private String memberHomePhone;

	@ExcelColumn(headerName = "이메일")
	@Email @NotBlank(message = "이메일 정보가 필요합니다")
	private String memberEmail;

	public void formatPhoneAndHomePhone() {
		memberHomePhone = FormatUtil.formatPhone(memberHomePhone);
		memberPhone = FormatUtil.formatPhone(memberPhone);
	}

	public Member toEntity(Vendor vendor) {
		Address mAddress = new Address(zipcode, address, addressDetail);
		return Member.builder()
			.vendor(vendor)
			.name(memberName)
			.phone(memberPhone != null ? memberPhone.replaceAll("[^0-9]", "") : null)
			.address(mAddress)
			.enrollDate(memberEnrollDate)
			.homePhone(memberHomePhone != null ? memberHomePhone.replaceAll("[^0-9]", "") : null)
			.email(memberEmail)
			.build();
	}
}
