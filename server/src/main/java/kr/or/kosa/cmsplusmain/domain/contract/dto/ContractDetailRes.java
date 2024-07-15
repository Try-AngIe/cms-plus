package kr.or.kosa.cmsplusmain.domain.contract.dto;

import java.time.LocalDateTime;
import java.util.List;

import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.payment.dto.PaymentDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContractDetailRes {

	private final Long contractId;                                	// 계약 ID
	private final String contractName;                              // 계약이름
	private final LocalDateTime createdDateTime;                	// 계약 등록일시
	private final LocalDateTime modifiedDateTime;                	// 계약 변경일시
	private final List<ContractProductRes> contractProducts;    	// 계약 상품 목록
	private final Long contractPrice;                            	// 계약금액
	private final PaymentDto payment;                            	// 결제정보

	public static ContractDetailRes fromEntity(Contract contract) {

		// NOT NULL
		final List<ContractProductRes> contractProductResList = contract.getContractProducts()
			.stream()
			.map(ContractProductRes::fromEntity)
			.toList();

		final PaymentDto paymentDto = PaymentDto.fromEntity(contract.getPayment());

		return ContractDetailRes.builder()
			.contractId(contract.getId())
			.contractName(contract.getName())
			.createdDateTime(contract.getCreatedDateTime())
			.modifiedDateTime(contract.getModifiedDateTime())
			.contractProducts(contractProductResList)
			.contractPrice(contract.getContractPrice())
			.payment(paymentDto)
			.build();
	}
}
