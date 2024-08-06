package kr.or.kosa.cmsplusmain.domain.contract.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusmain.domain.base.validator.Day;
import kr.or.kosa.cmsplusmain.domain.billing.entity.Billing;
import kr.or.kosa.cmsplusmain.domain.contract.exception.EmptyContractProductException;
import kr.or.kosa.cmsplusmain.domain.contract.validator.ContractName;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Comment("회원과 고객간의 계약 (학원 - 학생 간 계약)")
@Table(name = "contract")
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contract extends BaseEntity {

	// 최소 계약상품 수
	public static final int MIN_CONTRACT_PRODUCT_NUMBER = 1;
	public static final int MAX_CONTRACT_PRODUCT_NUMBER = 9;

	@Id
	@Column(name = "contract_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("계약한 회원의 고객")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "vendor_id")
	@NotNull
	private Vendor vendor;

	@Comment("계약한 회원")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "member_id")
	@NotNull
	private Member member;

	@Comment("계약 이름")
	@Column(name = "contract_name", nullable = false, length = 40)
	@ContractName
	@NotNull
	@Setter
	private String contractName;

	@Comment("계약 약정일")
	@Column(name = "contract_day", nullable = false)
	@Day
	@NotNull
	@Setter
	private Integer contractDay;

	@Comment("계약 결제정보")
	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name = "payment_id")
	@NotNull
	private Payment payment;

	@Comment("청구 가능 기간 - 시작일")
	@Column(name = "contract_start_date", nullable = false)
	@NotNull
	private LocalDate contractStartDate;

	@Comment("청구 가능 기간 - 종료일")
	@Column(name = "contract_end_date", nullable = false)
	@NotNull
	private LocalDate contractEndDate;

	@Comment("계약 상태")
	@Column(name = "contract_status", nullable = false)
	@NotNull
	@Builder.Default
	private ContractStatus contractStatus = ContractStatus.ENABLED;

	@Comment("계약금액 SUM(계약 상품 가격 * 수량) 미리 계산")
	@Column(name = "contract_contract_price", nullable = false)
	private Long contractPrice;

	@Comment("계약상품 수 미리 계산")
	@Column(name = "contract_contract_prodcut_cnt", nullable = false)
	private Integer contractProductCnt;

	/* 계약한 상품 목록 */
	// 계약 상품은 상품 ID 별로 하나만 존재할 수 있다.
	// 동일 상품 추가 안됨
	@OneToMany(mappedBy = "contract", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@SQLRestriction(BaseEntity.NON_DELETED_QUERY)
	@Builder.Default
	private Set<ContractProduct> contractProducts = new HashSet<>();

	/* 계약한 청구 목록 */
	@OneToMany(mappedBy = "contract", fetch = FetchType.LAZY)
	@SQLRestriction(BaseEntity.NON_DELETED_QUERY)
	@Builder.Default
	private List<Billing> billings = new ArrayList<>();

	/**
	 * id만 들고있는 빈 객체
	 * */
	public static Contract of(Long contractId) {
		Contract contract = new Contract();
		contract.id = contractId;
		return contract;
	}

	@PrePersist
	public void calculateContractPriceAndProductCnt() {
		this.contractPrice = contractProducts.stream()
			.mapToLong(ContractProduct::getContractProductPrice)
			.sum();

		this.contractProductCnt = contractProducts.size();
	}

	/**
	 * 계약 상품 추가
	 * */
	public void addContractProduct(ContractProduct contractProduct) {
		contractProducts.add(contractProduct);
		contractProduct.setContract(this);
	}

	/**
	 * 계약 상품 삭제
	 * */
	public void removeContractProduct(ContractProduct contractProduct) {
		// 계약은 최소 한 개 이상의 상품을 가져야한다.
		if (contractProducts.size() == MIN_CONTRACT_PRODUCT_NUMBER) {
			throw new EmptyContractProductException("계약은 최소 한 개 이상의 상품을 가져야합니다");
		}
		contractProduct.delete();
		contractProducts.remove(contractProduct);
	}

	/**
	 * 계약 삭제
	 * 계약상품, 청구, 결제도 동시 삭제됨
	 * */
	@Override
	public void delete() {
		super.delete();
		contractProducts.forEach(BaseEntity::delete);
		billings.forEach(BaseEntity::delete);
		payment.delete();
	}

	/**
	 * 계약 활성화 여부
	 * */
	public boolean isEnabled() {
		LocalDate curDate = LocalDate.now();
		return curDate.isBefore(contractEndDate);
	}
}
