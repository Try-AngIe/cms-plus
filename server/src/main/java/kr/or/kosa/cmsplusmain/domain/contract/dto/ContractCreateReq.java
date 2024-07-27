package kr.or.kosa.cmsplusmain.domain.contract.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.validator.Day;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractProduct;
import kr.or.kosa.cmsplusmain.domain.contract.validator.ContractName;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContractCreateReq {

	@ContractName @NotNull
	private String contractName;						// 계약명

	@NotNull
	private LocalDate contractStartDate;				// 계약 시작일

	@NotNull
	private LocalDate contractEndDate;					// 계약 종료일

	@Day
	@NotNull
	private Integer contractDay;						// 계약 약정일

	@NotNull
	private List<ContractProductReq> contractProducts;	// 계약상품 목록


    public Contract toEntity( Long vendorId, Member member, Payment payment) {
		return Contract.builder()
				.contractName(contractName)
				.contractStartDate(contractStartDate)
				.contractEndDate(contractEndDate)
				.contractDay(contractDay)
				.payment(payment)
				.vendor(Vendor.of(vendorId))
				.member(member)
				.build();
	}

	public List<ContractProduct> toProductEntities(Contract contract, Map<Long, String> idToName) {
		return contractProducts.stream()
				.map(contractProduct -> contractProduct.toEntity(contract, idToName.get(contractProduct.getProductId())))
				.collect(Collectors.toList());
	}
}

