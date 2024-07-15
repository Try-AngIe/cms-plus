package kr.or.kosa.cmsplusmain.domain.billing.entity;

import java.time.LocalDate;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusmain.domain.billing.exception.UpdateBillingDateException;
import kr.or.kosa.cmsplusmain.domain.billing.validator.InvoiceMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Comment("청구 (매 달 새로 쌓이는 정보)")
@Entity
@Table(name = "billing")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Billing extends BaseEntity {

	@Id
	@Column(name = "billing_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("청구 기준 정보")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "billing_standard_id")
	@NotNull
	private BillingStandard billingStandard;

	@Comment("결제일 (= 약정일, 납부 시작 및 종료 기간[납부기간은 하루이다.])")
	@Column(name = "billing_date", nullable = false)
	@NotNull
	private LocalDate billingDate;

	@Comment("청구 상태")
	@Enumerated(EnumType.STRING)
	@Column(name = "billing_status", nullable = false)
	@NotNull
	@Builder.Default
	private BillingStatus billingStatus = BillingStatus.CREATED;

	@Comment("청구서 메시지")
	@Column(name = "billing_invoice_message", length = 2000)
	@InvoiceMessage
	@Setter
	private String invoiceMessage;

	/*
	* 청구서명
	*
	* 결제일 기준으로
	* YYYY년 MM월 청구서 형식으로 자동생성
	* */
	public String getBillingName() {
		String year = Integer.toString(billingDate.getYear());
		String month = Integer.toString(billingDate.getMonthValue());
		return "%s년 %s월 청구서".formatted(year, month);
	}

	/*
	* 청구 결제일 수정
	* */
	public void setBillingDate(LocalDate billingDate) {
		// 청구의 결제일은 청구서 발송 전 상태에서만 수정 가능하다.
		if (!billingStatus.equals(BillingStatus.CREATED)) {
			throw new UpdateBillingDateException();
		}

		this.billingDate = billingDate;
	}
}
