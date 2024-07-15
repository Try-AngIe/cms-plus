package kr.or.kosa.cmsplusmain.domain.contract.dto;

import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractProduct;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContractProductRes {
	private Long contractProductId;		// 계약상품 ID
	private Long contractId;			// 계약 ID
	private Long productId;				// 상품 ID
	private String name;				// 상품명
	private Integer price;				// 계약상품 가격
	private Integer quantity;			// 계약상품 수량

	public static ContractProductRes fromEntity(ContractProduct contractProduct) {
		return ContractProductRes.builder()
			.contractProductId(contractProduct.getId())
			.contractId(contractProduct.getContract().getId())
			.productId(contractProduct.getProduct().getId())

			// 주의
			// 청구상품 과 청구 join 조회 필요
			.name(contractProduct.getProduct().getName())

			.price(contractProduct.getPrice())
			.quantity(contractProduct.getQuantity())
			.build();
	}
}
