package kr.or.kosa.cmsplusmain.domain.billing.entity;

import java.util.ArrayList;
import java.util.List;

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
import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusmain.domain.billing.exception.EmptyBillingProductException;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Comment("청구기준 = 청구 생성 정보 (정기적으로 혹은 추가 청구를 만들기 위한 정보)")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BillingStandard extends BaseEntity {

	private static final int MIN_BILLING_PRODUCT_NUMBER = 1;

	@Id
	@Column(name = "billing_standard_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("청구 기준 기반이된 계약")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "contract_id")
	@NotNull
	private Contract contract;

	@Comment("청구 기준 상태")
	@Enumerated(EnumType.STRING)
	@Column(name = "billing_standard_status", nullable = false)
	@NotNull
	private BillingStandardStatus status = BillingStandardStatus.ENABLED;

	@Comment("청구 타입 [정기 or 추가]")
	@Enumerated(EnumType.STRING)
	@Column(name = "billing_standard_type", nullable = false)
	@NotNull
	private BillingType type;

	@Comment("청구의 약정일 (청구 생성시 설정한 결제일 != 계약의 약정일과 다를 수 있다.)")
	@Column(name = "billing_standard_contract_day")
	private int contractDay;

	/* 청구 상품 목록 */
	@OneToMany(mappedBy = "billingStandard", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@SQLRestriction(BaseEntity.NON_DELETED_QUERY)
	private List<BillingProduct> billingProducts = new ArrayList<>();

	@Builder
	public BillingStandard(Contract contract, BillingType type, int contractDay, List<BillingProduct> billingProducts) {
		// 청구는 최소 한 개의 상품을 가져야한다.
		if (billingProducts.isEmpty()) {
			throw new EmptyBillingProductException();
		}

		this.contract = contract;
		this.type = type;
		this.contractDay = contractDay;

		billingProducts.forEach(this::addBillingProduct);
	}

	/*
	* 청구 상품 추가
	* */
	public void addBillingProduct(BillingProduct billingProduct) {
		billingProduct.setBillingStandard(this);
		billingProducts.add(billingProduct);
	}

	/*
	 * 청구 상품 삭제
	 * */
	public void removeBillingProduct(BillingProduct billingProduct) {
		// 청구는 최소 한 개 이상의 상품을 가져야한다.
		if (billingProducts.size() == MIN_BILLING_PRODUCT_NUMBER) {
			throw new EmptyBillingProductException();
		}
		billingProduct.delete();
		billingProducts.remove(billingProduct);
	}

	/*
	 * 청구금액
	 * */
	public long getBillingPrice() {
		return billingProducts.stream()
			.mapToLong(BillingProduct::getBillingProductPrice)
			.sum();
	}
}
