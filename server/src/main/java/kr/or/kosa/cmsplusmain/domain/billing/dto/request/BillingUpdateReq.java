package kr.or.kosa.cmsplusmain.domain.billing.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.or.kosa.cmsplusmain.domain.billing.entity.Billing;
import kr.or.kosa.cmsplusmain.domain.billing.validator.BillingDateReq;
import kr.or.kosa.cmsplusmain.domain.billing.validator.InvoiceMessage;
import lombok.Getter;

@Getter
public class BillingUpdateReq {
	@InvoiceMessage
	private String invoiceMessage;
	@NotNull
	@BillingDateReq
	private LocalDate billingDate;
	@NotNull
	@Size(
		min = Billing.MIN_BILLING_PRODUCT_NUMBER,
		message = Billing.MIN_BILLING_PRODUCT_NUMBER + "개 이상의 상품이 필요합니다")
	private List<@Valid BillingProductReq> billingProducts;
}
