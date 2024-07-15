package kr.or.kosa.cmsplusmain.domain.contract.dto;

import java.util.List;

import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractStatus;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.payment.entity.ConsentStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContractListItemRes {

	private final Long contractId;                                    	// 계약 ID
	private final String memberName;                                	// 회원 이름
	private final String memberPhone;                                	// 회원 휴대번호
	private final Integer contractDay;                                	// 약정일
	private final List<ContractProductRes> contractProducts;        	// 계약 상품 목록
	private final Long contractPrice;                                	// 계약금액
	private final ContractStatus contractStatus;                    	// 계약상태
	private final ConsentStatus consentStatus;                        	// 동의상태

	public static ContractListItemRes fromEntity(Contract contract) {

		// NOT NULL
		final Member member = contract.getMember();

		final List<ContractProductRes> contractProductResList = contract.getContractProducts()
			.stream()
			.map(ContractProductRes::fromEntity)
			.toList();

		final Payment payment = contract.getPayment();
		final ConsentStatus consentStatus = payment.getConsentStatus();

		return ContractListItemRes.builder()
			.contractId(contract.getId())
			.memberName(member.getName())
			.memberPhone(member.getPhone())
			.contractDay(contract.getContractDay())
			.contractProducts(contractProductResList)
			.contractPrice(contract.getContractPrice())
			.contractStatus(contract.getStatus())
			.consentStatus(consentStatus)
			.build();
	}
}
