package kr.or.kosa.cmsplusmain.domain.contract.dto;

import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractProduct;
import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductPrice;
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductQuantity;
import lombok.Getter;

@Getter
public class ContractProductReq {
	@NotNull private Long productId;	// 상품 ID
	@ProductPrice @NotNull
	private Integer price;				// 계약 상품 가격
	@ProductQuantity @NotNull
	private Integer quantity;			// 계약 상품 수량

	public ContractProduct toEntity() {
		return ContractProduct.builder()
			.product(Product.of(productId))
			.price(price)
			.quantity(quantity)
			.build();
	}
}
