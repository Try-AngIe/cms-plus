package kr.or.kosa.cmsplusmain.domain.billing.entity;

import java.time.LocalDate;
import java.util.List;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusmain.domain.base.OnlyNonSoftDeleted;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Comment("청구 (매 달 새로 쌓이는 정보)")
@Entity
@Table(name = "billing")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Billing extends BaseEntity {

	@Id
	@Column(name = "billing_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("청구 기준 정보")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_standard_id", nullable = false)
	@NotNull
	private BillingStandard billingStandard;

	@Comment("청구날짜 (매출에 잡히는 월)")
	@Column(name = "billing_date", nullable = false)
	@NotNull
	private LocalDate billingDate;

	@Comment("청구 상태")
	@Enumerated(EnumType.STRING)
	@Column(name = "billing_status", nullable = false)
	@NotNull
	private BillingStatus status;

	@Comment("청구서 메시지")
	@Column(name = "billing_memo", length = 2000)
	@Setter
	private String memo;

	@Comment("납부 기간 - 시작일")
	@Column(name = "billing_start_date", nullable = false)
	@NotNull
	private LocalDate billingStartDate;

	@Comment("납부 기간 - 종료일")
	@Column(name = "billing_end_date", nullable = false)
	@NotNull
	private LocalDate billingEndDate;

	/* 청구 상품 목록 */
	@OneToMany(mappedBy = "billing")
	@OnlyNonSoftDeleted
	private List<BillingProduct> billingProducts;
}