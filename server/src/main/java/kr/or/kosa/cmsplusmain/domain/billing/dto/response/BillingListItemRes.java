package kr.or.kosa.cmsplusmain.domain.billing.dto.response;

import java.time.LocalDate;

import com.querydsl.core.annotations.QueryProjection;

import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import lombok.Getter;

@Getter
public class BillingListItemRes {
	private final Long billingId;                // 청구 ID
	private final String memberName;            // 회원명
	private final String memberPhone;            // 휴대전화
	private final Long billingPrice;            // 청구금액
	private final BillingStatus billingStatus;    // 청구상태
	private final PaymentType paymentType;        // 결제방식
	private final LocalDate billingDate;        // 결제일
	private final String firstProductName;        // 목록에서 노출될 청구상품명
	private final Integer totalProductCount;    // 전체 청구상품 개수

	@QueryProjection
	public BillingListItemRes(Long billingId, String memberName, String memberPhone, Long billingPrice,
		BillingStatus billingStatus, PaymentType paymentType, LocalDate billingDate, String firstProductName,
		Integer totalProductCount) {
		this.billingId = billingId;
		this.memberName = memberName;
		this.memberPhone = memberPhone;
		this.billingPrice = billingPrice;
		this.billingStatus = billingStatus;
		this.paymentType = paymentType;
		this.billingDate = billingDate;
		this.firstProductName = firstProductName;
		this.totalProductCount = totalProductCount;
	}
}
