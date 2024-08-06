package kr.or.kosa.cmsplusmain.domain.billing.dto.request;

import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingProduct;
import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductName;
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductPrice;
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductQuantity;
import lombok.Getter;

@Getter
public class BillingProductReq {
	@NotNull(message = "상품 정보가 필요합니다")
	private Long productId;            // 상품 ID

	@ProductName
	@NotNull(message = "상품 정보가 필요합니다")
	private String name;            // 상품이름

	@ProductPrice
	@NotNull(message = "상품 가격이 설정되지 않았습니다")
	private Integer price;            // 청구 상품 가격

	@ProductQuantity
	@NotNull(message = "상품 수량이 설정되지 않았습니다")
	private Integer quantity;        // 청구 상품 개수

	public BillingProduct toEntity() {
		return BillingProduct.builder()
			.product(Product.of(productId))
			.name(name)
			.price(price)
			.quantity(quantity)
			.build();
	}
}
