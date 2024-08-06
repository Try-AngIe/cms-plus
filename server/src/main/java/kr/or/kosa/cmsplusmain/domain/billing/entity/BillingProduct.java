package kr.or.kosa.cmsplusmain.domain.billing.entity;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BillingProduct extends BaseEntity {

	@Id
	@Column(name = "billing_product_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("청구상품의 청구기준")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_id")
	@Setter
	private Billing billing;

	@Comment("청구상품의 상품")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id")
	private Product product;

	// 청구상품의 이름 수정불가
	// 조인 횟수 감소를 위해 상품 테이블에 있는 이름을 중복 저장하는 컬럼
	@Comment("청구상품의 이름")
	@Column(name = "billing_product_name", nullable = false, updatable = false)
	@ProductName
	@NotNull
	private String name;

	@Comment("청구상품의 가격")
	@Column(name = "billing_product_price", nullable = false)
	@ProductPrice
	private Integer price;

	@Comment("청구상품의 수량")
	@Column(name = "billing_product_quantity", nullable = false)
	@ProductQuantity
	private Integer quantity;

	/**
	 * 청구상품 수정
	 * */
	public void update(int price, int quantity) {
		this.price = price;
		this.quantity = quantity;
	}

	/**
	 * 청구상품 가격 (가격 * 수량)
	 * */
	public long getBillingProductPrice() {
		return (long)price * quantity;
	}

	/**
	 * 청구상품 동일성 비교 |
	 * 청구상품은 동일한 상품을 중복해서 지닐 수 없다.
	 * */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BillingProduct other)) {
			return false;
		}

		return (this.product.getId().equals(other.product.getId()));
	}

	@Override
	public int hashCode() {
		return product.getId().hashCode();
	}
}
