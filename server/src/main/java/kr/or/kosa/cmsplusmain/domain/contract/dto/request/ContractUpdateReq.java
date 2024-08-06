package kr.or.kosa.cmsplusmain.domain.contract.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.validator.ContractName;
import lombok.Getter;

@Getter
public class ContractUpdateReq {
	@ContractName
	@NotNull
	private String contractName;                        // 계약명

	@NotNull
	@Size(
		min = Contract.MIN_CONTRACT_PRODUCT_NUMBER,
		max = Contract.MAX_CONTRACT_PRODUCT_NUMBER,
		message = "상품 수량은 " + Contract.MIN_CONTRACT_PRODUCT_NUMBER + "~" + Contract.MAX_CONTRACT_PRODUCT_NUMBER)
	private List<@Valid ContractProductReq> contractProducts;    // 계약상품 목록
}
