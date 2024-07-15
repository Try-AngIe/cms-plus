package kr.or.kosa.cmsplusmain.domain.member.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import kr.or.kosa.cmsplusmain.domain.base.validator.HomePhone;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractProduct;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusmain.domain.base.validator.PersonName;
import kr.or.kosa.cmsplusmain.domain.base.validator.Phone;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.messaging.MessageSendMethod;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Comment("회원 (학원의 학생)")
@Entity
@Table(name = "member", uniqueConstraints = {
	@UniqueConstraint(name = "unique_member_email", columnNames = {"member_email"}),
	@UniqueConstraint(name = "unique_member_phone", columnNames = {"member_phone"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("회원의 고객")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vendor_id", updatable = false, nullable = false)
	@NotNull
	private Vendor vendor;

	/************ 기본정보 ************/

	@Comment("회원 상태")
	@Enumerated(EnumType.STRING)
	@Column(name = "member_status", nullable = false)
	@NotNull
	private MemberStatus status;

	@Comment("회원 이름")
	@Column(name = "member_name", nullable = false, length = 40)
	@PersonName
	@NotNull
	@Setter
	private String name;

	@Comment("회원 이메일")
	@Column(name = "member_email", nullable = false, length = 100)
	@Email
	@NotNull
	@Setter
	private String email;

	@Comment("회원 휴대전화")
	@Column(name = "member_phone", nullable = false, length = 20)
	@Phone
	@NotNull
	@Setter
	private String phone;

	@Comment("회원 유선전화")
	@Column(name = "member_homePhone", nullable = true, length = 20)
	@HomePhone
	@Setter
	private String homePhone;

	//TODO: 다음 도로명 주소 API 보고 수정하기
	@Comment("회원 주소")
	@Column(name = "member_address", nullable = true, length = 40)
	@Setter
	private String address;

	@Comment("회원 메모")
	@Column(name = "member_memo", nullable = true, length = 2000)
	@Size(max = 2000)
	@Setter
	private String memo;

	/* 학생이 학원에 등록한 날짜 (DB 생성일 X) */
	@Comment("회원 등록일")
	@Column(name = "member_enroll_date", nullable = false)
	@NotNull
	@Setter
	private LocalDate enrollDate;

	/************ 계약정보 ************/

	/* 회원이 맺은 계약 목록 */
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
	@SQLRestriction(BaseEntity.NON_DELETED_QUERY)
	private List<Contract> contracts = new ArrayList<>();

	/************ 청구정보 ************/

	@Comment("회원 청구서 발송 수단")
	@Enumerated(EnumType.STRING)
	@Column(name = "member_invoice_send_method", nullable = false)
	@NotNull
	@Setter
	private MessageSendMethod invoiceSendMethod;

	@Comment("회원 청구서 자동 발송 여부")
	@Column(name = "member_auto_invoice_send", nullable = false)
	@Setter
	private boolean autoInvoiceSend;

	@Comment("회원 청구 자동 생성 여부")
	@Column(name = "member_auto_billing", nullable = false)
	@Setter
	private boolean autoBilling;

	/*
	 * 계약금액합
	 * */
	public Long totalContractPrice() {
		return contracts.stream()
				.mapToLong(Contract::getContractPrice)
				.sum();
	}
}
