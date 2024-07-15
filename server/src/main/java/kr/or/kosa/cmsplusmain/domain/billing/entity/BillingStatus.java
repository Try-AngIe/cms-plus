package kr.or.kosa.cmsplusmain.domain.billing.entity;

import lombok.Getter;

@Getter
public enum BillingStatus {
	CREATED("생성"), WAITING_PAYMENT("수납 대기중"), PAID("완납"), NON_PAID("미납");

	private final String title;

	BillingStatus(String title) {
		this.title = title;
	}
}
