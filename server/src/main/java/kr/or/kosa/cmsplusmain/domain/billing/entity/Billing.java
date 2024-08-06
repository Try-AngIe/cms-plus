package kr.or.kosa.cmsplusmain.domain.billing.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLRestriction;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusmain.domain.billing.exception.EmptyBillingProductException;
import kr.or.kosa.cmsplusmain.domain.billing.exception.InvalidBillingStatusException;
import kr.or.kosa.cmsplusmain.domain.billing.exception.InvalidUpdateBillingException;
import kr.or.kosa.cmsplusmain.domain.billing.validator.InvoiceMessage;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO member, vendor 비정규화 여부 검토
@Comment("청구 (매 달 새로 쌓이는 정보)")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Billing extends BaseEntity {

	// 최소, 최대 청구상품 개수
	public static final int MIN_BILLING_PRODUCT_NUMBER = 1;
	public static final int MAX_BILLING_PRODUCT_NUMBER = 9;

	@Id
	@Column(name = "billing_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("청구 기준 기반이된 계약")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "contract_id")
	@NotNull
	private Contract contract;

	@Comment("청구 타입 [정기 or 추가]")
	@Enumerated(EnumType.STRING)
	@Column(name = "billing_type", nullable = false)
	@NotNull
	private BillingType billingType;

	@Comment("청구의 약정일 (청구 생성시 설정한 결제일 != 계약의 약정일과 다를 수 있다.)")
	@Column(name = "billing_contract_day", nullable = false)
	@NotNull
	private Integer contractDay;

	@Comment("결제일 (= 약정일, 납부 시작 및 종료 기간[납부기간은 하루이다.])")
	@Column(name = "billing_date", nullable = false)
	@NotNull
	private LocalDate billingDate;

	@Comment("청구 상태")
	@Enumerated(EnumType.STRING)
	@Column(name = "billing_status", nullable = false)
	@NotNull
	@Setter
	private BillingStatus billingStatus = BillingStatus.CREATED;

	@Comment("청구서 메시지")
	@Column(name = "billing_invoice_message", length = 2000)
	@InvoiceMessage
	@Setter
	private String invoiceMessage;

	@Comment("청구서 보낸시각")
	@Column(name = "billing_invoice_send_datetime")
	@Setter
	private LocalDateTime invoiceSendDateTime;

	@Comment("청구 결제된 시각")
	@Column(name = "billing_paid_datetime")
	@Setter
	private LocalDateTime paidDateTime;

	/* 청구 상품 목록 */
	// 청구 상품은 상품 ID 별로 하나만 존재할 수 있다.
	// 동일 상품 추가 안됨
	@OneToMany(mappedBy = "billing", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@SQLRestriction(BaseEntity.NON_DELETED_QUERY)
	private final Set<BillingProduct> billingProducts = new HashSet<>();

	@Comment("청구금액 SUM(청구 상품 가격 * 수량) 미리 계산")
	@Column(name = "billing_billing_price", nullable = false)
	private long billingPrice;

	@Comment("청구상품 수 미리 계산")
	@Column(name = "billing_billing_prodcut_cnt", nullable = false)
	private int billingProductCnt;

	public Billing(Contract contract, BillingType billingType, LocalDate billingDate, int contractDay,
		List<BillingProduct> billingProducts) {
		// 청구는 최소 한 개의 상품을 가져야한다.
		if (billingProducts.isEmpty()) {
			throw new EmptyBillingProductException("청구는 최소 한 개 이상의 상품을 가져야합니다");
		}

		this.contract = contract;
		this.billingType = billingType;
		this.contractDay = contractDay;
		this.billingDate = billingDate;

		billingProducts.forEach(this::addBillingProduct);
	}

	/**
	 * 청구 상품만 수정하는 경우 호출 안되므로 청구 상품 변경 메서드에서
	 * 메서드 호출 필수
	 * 목록 조회 성능 개선을 위한 미리 계산
	 * */
	@PrePersist
	public void calculateBillingPriceAndProductCnt() {
		this.billingPrice = billingProducts.stream()
			.mapToLong(BillingProduct::getBillingProductPrice)
			.sum();

		this.billingProductCnt = billingProducts.size();
	}

	/**
	 * 청구 상품 추가
	 * */
	public void addBillingProduct(BillingProduct billingProduct) {
		billingProduct.setBilling(this);
		billingProducts.add(billingProduct);
	}

	/**
	 * 청구 상품 삭제 | 청구는 최소 1개의 상품을 지녀야한다.
	 * @throws EmptyBillingProductException 청구상품이 1개인 경우
	 * */
	public void removeBillingProduct(BillingProduct billingProduct) {
		if (billingProducts.size() == MIN_BILLING_PRODUCT_NUMBER) {
			throw new EmptyBillingProductException("청구는 최소 한 개 이상의 상품을 가져야합니다");
		}
		billingProduct.delete();
		billingProducts.remove(billingProduct);
	}

	/**
	 * 청구서명
	 * @return "YYYY년 MM월 청구서" (결제일 기준)
	 * */
	public String getInvoiceName() {
		String year = Integer.toString(billingDate.getYear());
		String month = Integer.toString(billingDate.getMonthValue());
		return "%s년 %s월 청구서".formatted(year, month);
	}

	/**
	 * 청구 결제일 수정 | 청구의 결제일은 오늘 이후 날짜로만 수정가능하다.
	 * @throws InvalidUpdateBillingException 청구 결제일 수정 불가
	 * */
	public void updateBillingDate(LocalDate billingDate) {
		if (this.billingDate.equals(billingDate)) {
			return;
		}
		if (billingDate.isBefore(LocalDate.now())) {
			throw new InvalidUpdateBillingException("청구 결제일은 이미 지난 날짜로 설정될 수 없습니다");
		}
		this.billingDate = billingDate;
	}

	/**
	 * 청구서 발송 완료
	 * @throws InvalidBillingStatusException 청구서 발송 불가능인경우
	 * */
	public void setInvoiceSent() {
		BillingState.Field.SEND_INVOICE.validateState(this);
		billingStatus = BillingStatus.WAITING_PAYMENT;
		invoiceSendDateTime = LocalDateTime.now();
	}

	/**
	 * 청구서 발송 취소 상태 변경 | 청구생성 상태로 돌아간다.
	 * @throws InvalidBillingStatusException 청구서 발송 취소가 불가능한 경우
	 */
	public void setInvoiceCancelled() {
		BillingState.Field.CANCEL_INVOICE.validateState(this);
		billingStatus = BillingStatus.CREATED;
		invoiceSendDateTime = null;
	}

	/**
	 * 청구 결제 완료 상태변경
	 * @throws InvalidBillingStatusException 이미 결제된 청구인 경우
	 */
	public void setPaid() {
		BillingState.Field.PAY.validateState(this);
		billingStatus = BillingStatus.PAID;
		paidDateTime = LocalDateTime.now();
	}

	/**
	 * 청구 결제 취소 상태 변경
	 * @throws InvalidBillingStatusException 결제 취소가 불가능한 경우
	 */
	public void setPayCanceled() {
		BillingState.Field.CANCEL_PAYMENT.validateState(this);
		billingStatus = BillingStatus.WAITING_PAYMENT;
		paidDateTime = null;
	}

	/**
	 * 청구 삭제 | 연관된 청구상품도 삭제된다.
	 * @throws InvalidBillingStatusException 납부 전에만 삭제가능
	 */
	@Override
	public void delete() {
		BillingState.Field.DELETE.validateState(this);
		super.delete();
		billingProducts.forEach(BillingProduct::delete);
	}

	/**
	 * 청구 삭제 | 연관된 청구상품은 삭제되지 않는다.
	 * @throws InvalidBillingStatusException 납부 전에만 삭제가능
	 */
	public void deleteWithoutProducts() {
		BillingState.Field.DELETE.validateState(this);
		super.delete();
	}
}
