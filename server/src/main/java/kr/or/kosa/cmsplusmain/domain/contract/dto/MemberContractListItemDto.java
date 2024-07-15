package kr.or.kosa.cmsplusmain.domain.contract.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;

import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.ConsentStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberContractListItemDto {
    private final Long contractId;                                    // 계약 ID
    private final String contractName;
    // TODO: LocalDate로 리턴 후 프론트에서 처리
    private final String contractDate;
    private final int contractDay;
    private final List<ContractProductRes> contractProducts;
    private final Long contractPrice ;
    private final ContractStatus contractStatus;
    private final ConsentStatus consentStatus;

    public static MemberContractListItemDto fromEntity(Contract contract) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String formattedContractDate = contract.getContractStartDate().format(formatter) + " ~ " + contract.getContractEndDate().format(formatter);

        final List<ContractProductRes> contractProductResList = contract.getContractProducts()
                .stream()
                .map(ContractProductRes::fromEntity)
                .toList();

        final Payment payment = contract.getPayment();
        final ConsentStatus consentStatus = payment.getConsentStatus();

        return MemberContractListItemDto.builder()
                .contractId(contract.getId())
                .contractName(contract.getName())
                .contractDate(formattedContractDate)
                .contractDay(contract.getContractDay())
                .contractProducts(contractProductResList)
                .contractPrice(contract.getContractPrice())
                .contractStatus(contract.getStatus())
                .consentStatus(consentStatus)
                .build();
    }

}
