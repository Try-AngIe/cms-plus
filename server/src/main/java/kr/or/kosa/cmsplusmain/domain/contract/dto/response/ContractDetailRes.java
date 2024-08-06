package kr.or.kosa.cmsplusmain.domain.contract.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.kafka.MessageSendMethod;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.payment.dto.method.PaymentMethodInfoRes;
import kr.or.kosa.cmsplusmain.domain.payment.dto.type.PaymentTypeInfoRes;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContractDetailRes {

	private final Long contractId;                                    // 계약 ID
	private final Long memberId;                                    // 회원 ID
	private final String contractName;                              // 계약이름
	private final LocalDateTime createdDateTime;                    // 계약 등록일시
	private final LocalDateTime modifiedDateTime;                    // 계약 변경일시
	private final List<ContractProductRes> contractProducts;        // 계약 상품 목록
	private final Long contractPrice;                                // 계약금액
	private final Integer contractDay;                                // 약정일

	private final LocalDate contractStartDate;                        // 계약 시작일
	private final LocalDate contractEndDate;                        // 계약 종료일

	private final PaymentTypeInfoRes paymentTypeInfo;               // 결제방식
	private final PaymentMethodInfoRes paymentMethodInfo;            // 결제수단

	private final Boolean autoBilling;                                // 자동 청구 생성
	private final Boolean autoInvoiceSend;                            // 자동 청구서 발송
	private final MessageSendMethod invoiceSendMethod;                // 청구서 발송 수단
	private final String payerPhone;                                // 납부자 휴대전화
	private final String payerEmail;                                // 납부자 이메일

	private final Long totalBillingCount;                            // 청구 총 개수
	private final Long totalBillingPrice;                            // 청구 총 금액

	public static ContractDetailRes fromEntity(
		Contract contract,
		PaymentTypeInfoRes paymentTypeInfoRes, PaymentMethodInfoRes paymentMethodInfoRes,
		Long totalBillingCount, Long totalBillingPrice) {

		// NOT NULL
		final List<ContractProductRes> contractProductResList = contract.getContractProducts()
			.stream()
			.map(ContractProductRes::fromEntity)
			.toList();

		final Member member = contract.getMember();

		return ContractDetailRes.builder()
			.memberId(contract.getMember().getId())
			.contractId(contract.getId())
			.contractName(contract.getContractName())
			.createdDateTime(contract.getCreatedDateTime())
			.modifiedDateTime(contract.getModifiedDateTime())
			.contractProducts(contractProductResList)
			.contractPrice(contract.getContractPrice())
			.contractStartDate(contract.getContractStartDate())
			.contractEndDate(contract.getContractEndDate())
			.contractDay(contract.getContractDay())
			.paymentTypeInfo(paymentTypeInfoRes)
			.paymentMethodInfo(paymentMethodInfoRes)
			.autoBilling(member.isAutoBilling())
			.autoInvoiceSend(member.isAutoInvoiceSend())
			.invoiceSendMethod(member.getInvoiceSendMethod())
			.payerPhone(member.getPhone())
			.payerEmail(member.getEmail())
			.totalBillingCount(totalBillingCount)
			.totalBillingPrice(totalBillingPrice)
			.build();
	}
}
