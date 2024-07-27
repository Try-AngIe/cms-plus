package kr.or.kosa.cmsplusmain.domain.vendor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.validator.HomePhone;
import kr.or.kosa.cmsplusmain.domain.base.validator.PersonName;
import kr.or.kosa.cmsplusmain.domain.base.validator.Phone;
import kr.or.kosa.cmsplusmain.domain.base.validator.Username;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.UserRole;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import kr.or.kosa.cmsplusmain.domain.vendor.validator.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class SignupReq {

	@Username
	@NotNull
	private String username;

	@Password
	@NotNull
	private String password;

	@PersonName
	@NotNull
	private String name;

	@Email
	@NotNull
	private String email;

	@Phone
	@NotNull
	private String phone;

	@HomePhone
	private String homePhone;

	@NotBlank
	private String department;

	public Vendor toEntity(String username, String password, UserRole role) {
		return Vendor.builder()
			.username(username)
			.password(password)
			.name(name)
			.email(email)
			.phone(phone)
			.homePhone(homePhone)
			.department(department)
			.role(role)
			.build();
	}
}
