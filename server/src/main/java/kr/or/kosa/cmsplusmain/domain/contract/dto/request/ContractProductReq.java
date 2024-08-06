package kr.or.kosa.cmsplusmain.domain.contract.dto.request;

import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractProduct;
import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductPrice;
import kr.or.kosa.cmsplusmain.domain.product.validator.ProductQuantity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ContractProductReq {
	@NotNull(message = "상품 정보가 필요합니다")
	private Long productId;                // 상품 ID

	@ProductPrice
	@NotNull(message = "상품 가격이 설정되지 않았습니다")
	private Integer price;                // 계약 상품 가격

	@ProductQuantity
	@NotNull(message = "상품 수량이 설정되지 않았습니다")
	private Integer quantity;            // 계약 상품 수량

	public ContractProduct toEntity(String name) {
		return ContractProduct.builder()
			.product(Product.of(productId))
			.name(name)
			.price(price)
			.quantity(quantity)
			.build();
	}

	public ContractProduct toEntity(Contract contract, String name) {
		return ContractProduct.builder()
			.product(Product.of(productId))
			.name(name)
			.contract(contract)
			.price(price)
			.quantity(quantity)
			.build();
	}
}