package kr.or.kosa.cmsplusmain.domain.vendor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import kr.or.kosa.cmsplusmain.domain.base.validator.HomePhone;
import kr.or.kosa.cmsplusmain.domain.base.validator.PersonName;
import kr.or.kosa.cmsplusmain.domain.base.validator.Phone;
import kr.or.kosa.cmsplusmain.domain.base.validator.Username;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.UserRole;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import kr.or.kosa.cmsplusmain.domain.vendor.validator.Password;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignupDto {

	@Username
	private String username;

	@Password
	private String password;

	@PersonName
	private String name;

	@Email
	private String email;

	@Phone
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
