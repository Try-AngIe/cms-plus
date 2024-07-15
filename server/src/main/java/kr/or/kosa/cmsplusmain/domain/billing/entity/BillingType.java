package kr.or.kosa.cmsplusmain.domain.billing.entity;

import lombok.Getter;

@Getter
public enum BillingType {
	REGULAR("정기"), IRREGULAR("추가");

	private final String title;

	BillingType(String title) {
		this.title = title;
	}
}
