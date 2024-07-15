package kr.or.kosa.cmsplusmain.domain.contract.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class ContractCreateReq {
	private String contractName;	// 계약명
	private List<ContractProductReq> contractProducts;	// 계약상품 목록
}
