package kr.or.kosa.cmsplusmain.domain.billing.dto;

import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingProduct;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BillingProductRes {
	private Long billingProductId;		// 청구상품 ID
	private Long billingStandardId;		// 청구기준 ID
	private Long productId;				// 상품 ID
	private String name;				// 상품명
	private Integer price;				// 청구상품 가격
	private Integer quantity;			// 청구상품 수량

	public static BillingProductRes fromEntity(BillingProduct billingProduct) {
		return BillingProductRes.builder()
			.billingProductId(billingProduct.getId())
			.billingStandardId(billingProduct.getBillingStandard().getId())
			.productId(billingProduct.getProduct().getId())

			// 주의
			// 청구상품 과 상품 fetch join 조회 필요
			.name(billingProduct.getProduct().getName())

			.price(billingProduct.getPrice())
			.quantity(billingProduct.getQuantity())
			.build();
	}
}
