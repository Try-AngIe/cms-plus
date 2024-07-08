package kr.or.kosa.cmsplusmain.domain.billing.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingProduct;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.PaymentType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BillingListItem {

	private final Long billingId;
	private final String memberName;
	private final LocalDate billingDate;
	private final List<BillingProductDto.Res> billingProducts;
	private final Long billingPrice;
	private final String billingStatus;
	private final String paymentType;
	private final LocalDate contractDate;

	@Builder
	public BillingListItem(Long billingId, String memberName, LocalDate billingDate,
		List<BillingProduct> billingProducts,
		BillingStatus billingStatus, PaymentType paymentType, LocalDate contractDate) {

		this.billingId = billingId;
		this.memberName = memberName;
		this.billingDate = billingDate;
		this.billingProducts = (billingProducts == null) ? Collections.emptyList() :
			billingProducts.stream().map(BillingProductDto.Res::fromEntity)
				.toList();
		this.billingPrice = (billingProducts != null) ? billingProducts.stream()
			.mapToLong(BillingProduct::getTotalPrice)
			.sum() : null;
		this.billingStatus = (billingStatus != null) ? billingStatus.getTitle() : null;
		;
		this.paymentType = (paymentType != null) ? paymentType.getTitle() : null;
		this.contractDate = contractDate;
	}
}
