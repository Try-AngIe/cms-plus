package kr.or.kosa.cmsplusmain.test.domain.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TProductUpdateReq { // 상품 수정

	private String name;
	private double price;
	private String memo;

}
