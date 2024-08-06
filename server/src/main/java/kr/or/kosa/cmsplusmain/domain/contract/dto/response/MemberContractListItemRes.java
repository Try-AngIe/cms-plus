package kr.or.kosa.cmsplusmain.domain.contract.dto.response;

import java.time.LocalDate;
import java.util.List;

import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberContractListItemRes {
	private final Long contractId;                                    // 계약 ID
	private final String contractName;
	private final LocalDate contractStartDate;
	private final LocalDate contractEndDate;
	private final int contractDay;
	private final List<ContractProductRes> contractProducts;
	private final Long contractPrice;
	private final PaymentType paymentType;                              // 결제방식
	private final ContractStatus contractStatus;                              // 계약상태

	public static MemberContractListItemRes fromEntity(Contract contract) {
		final List<ContractProductRes> contractProductResList = contract.getContractProducts()
			.stream()
			.map(ContractProductRes::fromEntity)
			.toList();

		final Payment payment = contract.getPayment();

		return MemberContractListItemRes.builder()
			.contractId(contract.getId())
			.contractName(contract.getContractName())
			.contractStartDate(contract.getContractStartDate())
			.contractEndDate(contract.getContractEndDate())
			.contractDay(contract.getContractDay())
			.contractProducts(contractProductResList)
			.contractPrice(contract.getContractPrice())
			.paymentType(payment.getPaymentType())
			.contractStatus(contract.getContractStatus())
			.build();
	}

}
