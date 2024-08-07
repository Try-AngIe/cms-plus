package kr.or.kosa.cmsplusbatch.domain.vendor.entity;

import kr.or.kosa.cmsplusbatch.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusbatch.domain.settings.entity.SimpConsentSetting;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.Comment;

import jakarta.persistence.CascadeType;
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

@Comment("고객 (학원의 원장 - 사용자)")
@Entity
//TODO unique soft delete
@Table(name = "vendor", uniqueConstraints = {
	@UniqueConstraint(name = "unique_vendor_username", columnNames = {"vendor_username"}),
	@UniqueConstraint(name = "unique_vendor_email", columnNames = {"vendor_email"}),
	@UniqueConstraint(name = "unique_vendor_phone", columnNames = {"vendor_phone"})
})

//TODO
// Build, ALL, No 이렇게 세개 전부다 사용해도 괜찮을까

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
	@NotNull
	private String username;

	@Comment("고객 로그인 비밀번호")
	@Column(name = "vendor_password", nullable = false)
	@NotNull
	@Setter
	private String password;

	@Comment("고객 이름")
	@Column(name = "vendor_name", nullable = false, length = 40)
	@NotNull
	private String name;

	@Comment("고객 이메일")
	@Column(name = "vendor_email", nullable = false, length = 100)
	@NotNull
	private String email;

	@Comment("고객 휴대전화")
	@Column(name = "vendor_phone", nullable = false, length = 20)
	@NotNull
	private String phone;

	@Comment("고객 유선전화")
	@Column(name = "vendor_homephone", length = 20)
	private String homePhone;

	@Comment("고객 부서명")
	@Column(name = "vendor_dept", nullable = false, length = 40)
	private String department;

	@Comment("사용자 역할")
	@Enumerated(EnumType.STRING)
	@Column(name = "user_role", nullable = false)
	@NotNull
	private UserRole role;

	/* 간편동의 설정 */
	// TODO: 고객 아이디 만들기 시 최초 설정 값 지녀야함
	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name = "setting_simpconset_id")
	private SimpConsentSetting simpConsentSetting;

	public void setSimpConsentSetting(SimpConsentSetting simpConsentSetting) {
		this.simpConsentSetting = simpConsentSetting;
		simpConsentSetting.setVendor(this);
	}

	public static Vendor of(Long id) {
		Vendor emptyVendor = new Vendor();
		emptyVendor.id = id;
		return emptyVendor;
	}
}
