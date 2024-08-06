package kr.or.kosa.cmsplusmain.domain.contract.entity;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductName;
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductPrice;
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductQuantity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Comment("계약 - 상품 중계테이블. 상품 변동에 영향을 받지 않는다.")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractProduct extends BaseEntity {

	@Id
	@Column(name = "contract_product_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "contract_id")
	@Setter
	private Contract contract;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id")
	@NotNull
	private Product product;

	@Comment("계약_상품 이름")
	@Column(name = "contract_product_name", nullable = false, updatable = false)
	@ProductName
	@NotNull
	private String name;

	@Comment("계약_상품 가격")
	@Column(name = "contract_product_price")
	@ProductPrice
	private int price;

	@Comment("계약_상품 수량")
	@Column(name = "contract_product_quantity")
	@ProductQuantity
	private int quantity;

	@Builder
	public ContractProduct(Contract contract, Product product, String name, int price, int quantity) {
		this.contract = contract;
		this.product = product;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
	}

	/**
	 * 계약상품 수정
	 * */
	public void update(int price, int quantity) {
		this.price = price;
		this.quantity = quantity;
	}

	/**
	 * 계약 상품 계산된 금액
	 * */
	public long getContractProductPrice() {
		return (long)price * quantity;
	}

	/**
	 * 계약상품 동일성 비교
	 * 기반이된 상품
	 * 동일 상품 1개만 가질 수 있다.
	 * */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContractProduct other)) {
			return false;
		}

		return (this.product.getId().equals(other.product.getId()));
	}

	@Override
	public int hashCode() {
		return product.getId().hashCode();
	}
}
