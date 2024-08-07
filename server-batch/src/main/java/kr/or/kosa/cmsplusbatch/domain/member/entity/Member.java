package kr.or.kosa.cmsplusbatch.domain.member.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import kr.or.kosa.cmsplusbatch.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusbatch.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusbatch.batch.MessageSendMethod;
import kr.or.kosa.cmsplusbatch.domain.vendor.entity.Vendor;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLRestriction;

import lombok.Builder;


@Comment("회원 (학원의 학생)")
@Entity
@Table(name = "member", uniqueConstraints = {
	@UniqueConstraint(name = "unique_member_email", columnNames = {"vendor_id", "member_email"}),
	@UniqueConstraint(name = "unique_member_phone", columnNames = {"vendor_id", "member_phone"})})
@Getter
@AllArgsConstructor
@Builder
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
	@Builder.Default
	private MemberStatus status = MemberStatus.ENABLED;

	@Comment("회원 이름")
	@Column(name = "member_name", nullable = false, length = 40)
	@NotNull
	@Setter
	private String name;

	@Comment("회원 이메일")
	@Column(name = "member_email", nullable = false, length = 100)
	@NotNull
	@Setter
	private String email;

	@Comment("회원 휴대전화")
	@Column(name = "member_phone", nullable = false, length = 20)
	@NotNull
	@Setter
	private String phone;

	@Comment("회원 유선전화")
	@Column(name = "member_homePhone", nullable = true, length = 20)
	@Setter
	private String homePhone;

	@Comment("회원 주소")
	@Embedded
	@Setter
	private Address address;

	@Comment("회원 메모")
	@Column(name = "member_memo", nullable = true, length = 2000)
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
	@Builder.Default
	private List<Contract> contracts = new ArrayList<>();

	/************ 청구정보 ************/

	@Comment("회원 청구서 발송 수단")
	@Enumerated(EnumType.STRING)
	@Column(name = "member_invoice_send_method", nullable = false)
	@NotNull
	@Builder.Default
	@Setter
	private MessageSendMethod invoiceSendMethod = MessageSendMethod.EMAIL;

	@Comment("회원 청구서 자동 발송 여부")
	@Column(name = "member_auto_invoice_send", nullable = false)
	@Setter
	@Builder.Default
	private boolean autoInvoiceSend = true;

	@Comment("회원 청구 자동 생성 여부")
	@Column(name = "member_auto_billing", nullable = false)
	@Setter
	@Builder.Default
	private boolean autoBilling = true;

	/*
	 * 계약금액합
	 * */
	public Long totalContractPrice() {
		return contracts.stream()
				.mapToLong(Contract::getContractPrice)
				.sum();
	}

	/*
	* 총 계약 수
	* */
	public int getContractNum() {
		return contracts.size();
	}

	/*
	 * 총 계약 수
	 * 계약이 함께 삭제된다
	 * */
	@Override
	public void delete() {
		super.delete();
		contracts.forEach(Contract::delete);
	}
}
