package kr.or.kosa.cmsplusmain.domain.billing.dto.request;

import java.time.LocalDate;

import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter    // requestParam 으로 사용을 위한 setter
public class BillingSearchReq {

	/****** 검색 가능 항목 *******/

	private String memberName;                // 회원 이름 포함
	private String memberPhone;                // 회원 휴대전화 포함
	private Long billingPrice;                // 청구금액 이하
	private String productName;                // 청구상품 목록 중 상품 이름 포함
	private BillingStatus billingStatus;    // 청구상태 일치
	private PaymentType paymentType;        // 결제방식 일치
	private LocalDate billingDate;            // 청구 결제일 일치
	private Long contractId;                // 계약 ID

	/****** 정렬 가능 항목 *******/
	// memberName
	// billingPrice
	// billingDate
}
