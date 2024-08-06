package kr.or.kosa.cmsplusmain.domain.contract.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class V2ContractListItemRes {

	private final Long contractId;                                        // 계약 ID
	private final String memberName;                                    // 회원 이름
	private final String memberPhone;                                    // 회원 휴대번호
	private final Integer contractDay;                                    // 약정일
	private final Long contractPrice;                                    // 계약금액
	private final PaymentType paymentType;                                // 결제방식
	private final ContractStatus contractStatus;                        // 계약상태
	private final Integer totalProductCount;                                // 전체 계약상품 수
	@Setter
	private String firstProductName;                            // 첫번째 상품명

	@QueryProjection
	public V2ContractListItemRes(Long contractId, String memberName, String memberPhone, Integer contractDay,
		Long contractPrice, PaymentType paymentType, ContractStatus contractStatus, String firstProductName,
		Integer totalProductCount) {
		this.contractId = contractId;
		this.memberName = memberName;
		this.memberPhone = memberPhone;
		this.contractDay = contractDay;
		this.contractPrice = contractPrice;
		this.paymentType = paymentType;
		this.contractStatus = contractStatus;
		this.firstProductName = firstProductName;
		this.totalProductCount = totalProductCount;
	}
}
