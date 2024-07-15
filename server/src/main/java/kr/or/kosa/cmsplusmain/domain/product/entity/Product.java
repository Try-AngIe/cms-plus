package kr.or.kosa.cmsplusmain.domain.product.entity;

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
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductMemo;
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductName;
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductPrice;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Comment("고객이 등록한 상품")
@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

	@Id
	@Column(name = "product_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("상품을 등록한 고객")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vendor_id", updatable = false, nullable = false)
	@NotNull
	private Vendor vendor;

	@Comment("상품 상태")
	@Enumerated(EnumType.STRING)
	@Column(name = "product_status", nullable = false)
	@NotNull
	private ProductStatus status;

	@Comment("상품 이름")
	@Column(name = "product_name", nullable = false, length = 20)
	@ProductName
	@NotNull
	private String name;

	@Comment("상품 금액")
	@Column(name = "product_price", nullable = false)
	@ProductPrice
	@Setter
	private int price;

	@Comment("상품 비고")
	@Column(name = "product_memo", length = 2000)
	@ProductMemo
	@Setter
	private String memo = "";

	// id값만 가진 빈 product를 생성
	public static Product of(Long id) {
		Product emptyProduct = new Product();
		emptyProduct.id = id;
		return emptyProduct;
	}
}
