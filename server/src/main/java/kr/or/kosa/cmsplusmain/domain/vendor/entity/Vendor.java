package kr.or.kosa.cmsplusmain.domain.vendor.entity;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusmain.domain.base.validator.HomePhone;
import kr.or.kosa.cmsplusmain.domain.base.validator.PersonName;
import kr.or.kosa.cmsplusmain.domain.base.validator.Phone;
import kr.or.kosa.cmsplusmain.domain.base.validator.Username;
import kr.or.kosa.cmsplusmain.domain.settings.entity.SimpConsentSetting;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Comment("고객 (학원의 원장 - 사용자)")
@Entity
//TODO unique soft delete
@Table(name = "vendor", uniqueConstraints = {
	@UniqueConstraint(name = "unique_vendor_username", columnNames = {"vendor_username"}),
	@UniqueConstraint(name = "unique_vendor_email", columnNames = {"vendor_email"}),
	@UniqueConstraint(name = "unique_vendor_phone", columnNames = {"vendor_phone"})
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vendor extends BaseEntity {

	@Id
	@Column(name = "vendor_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/************ 고객 기본정보 ************/

	@Comment("고객 로그인 아이디")
	@Column(name = "vendor_username", nullable = false, length = 20)
	@Username
	@NotNull
	private String username;

	@Comment("고객 로그인 비밀번호")
	@Column(name = "vendor_password", nullable = false)
	@NotNull
	private String password;

	@Comment("고객 이름")
	@Column(name = "vendor_name", nullable = false, length = 40)
	@PersonName
	@NotNull
	private String name;

	@Comment("고객 이메일")
	@Column(name = "vendor_email", nullable = false, length = 100)
	@Email
	@NotNull
	private String email;

	@Comment("고객 휴대전화")
	@Column(name = "vendor_phone", nullable = false, length = 20)
	@Phone
	@NotNull
	private String phone;

	@Comment("고객 유선전화")
	@Column(name = "vendor_homephone", length = 20)
	@HomePhone
	private String homePhone;

	@Comment("고객 부서명")
	@Column(name = "vendor_dept", nullable = false, length = 40)
	@NotBlank
	private String department;

	@Comment("사용자 역할")
	@Enumerated(EnumType.STRING)
	@Column(name = "user_role", nullable = false)
	@NotNull
	private UserRole role;

	/* 간편동의 설정 */
	// TODO: 고객 아이디 만들기 시 최초 설정 값 지녀야함
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "setting_simpconset_id")
	private SimpConsentSetting simpConsentSetting;

	public void setSimpConsentSetting(SimpConsentSetting simpConsentSetting) {
		this.simpConsentSetting = simpConsentSetting;
		simpConsentSetting.setVendor(this);
	}
}
