package kr.or.kosa.cmsplusmain.domain.contract.dto;

import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.ConsentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractSearchReq {

	/****** 검색 가능 항목 *******/

	private String memberName;
	private String memberPhone;
	private Integer contractDay;
	private Long contractPrice;
	private String productName;
	private ContractStatus contractStatus;
	private ConsentStatus consentStatus;

	/****** 정렬 가능 항목 *******/
	// memberName
	// contractDay
	// contractPrice
}
