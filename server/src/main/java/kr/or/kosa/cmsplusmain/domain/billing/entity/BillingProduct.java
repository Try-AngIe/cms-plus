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
import jakarta.persistence.Table;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductPrice;
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductQuantity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "billing_product")
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
	@JoinColumn(name = "billing_standard_id")
	@Setter
	private BillingStandard billingStandard;

	/*
	 * 청구 상품이 사용될 때 상품의 이름이 항상 같이 사용된다.
	 * fetch eager -> 쿼리 횟수 감소
	 * */
	@Comment("청구상품의 상품")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_id")
	private Product product;

	@Comment("청구상품의 가격")
	@Column(name = "billing_product_price")
	@ProductPrice
	private int price;

	@Comment("청구상품의 수량")
	@Column(name = "billing_product_quantity")
	@ProductQuantity
	private int quantity;

	/*
	* 청구상품 가격 (상품 * 수량)
	* */
	public long getBillingProductPrice() {
		return (long)price * quantity;
	}

	/*
	* 청구상품 동일성 비교
	* 기반이된 상품, 청구상품 가격, 청구상품 수량
	* */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BillingProduct other)) {
			return false;
		}

		return (this.product.getId().equals(other.product.getId()))
			&& (this.price == other.price)
			&& (this.quantity == other.quantity);
	}
}
