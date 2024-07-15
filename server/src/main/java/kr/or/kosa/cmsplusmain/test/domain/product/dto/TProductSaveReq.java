package kr.or.kosa.cmsplusmain.test.domain.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TProductSaveReq { // 상품 등록

	private Long id;
	private Long vendorId;
	private String name;
	private double price;
	private String createdDateTime;
	private String memo;

}
