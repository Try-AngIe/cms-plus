package kr.or.kosa.cmsplusmain.test.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TProductDto { // 상품 정보(원래는 이거 엔티티로 하고 DTO 따로 운영해야함)

	private Long id;
	private Long vendorId;
	private String name;
	private double price;
	private int contractCount;
	private String createdDateTime;
	private String updatedDateTime;
	private String deletedDate;
	private String memo;
	private String status;

}
