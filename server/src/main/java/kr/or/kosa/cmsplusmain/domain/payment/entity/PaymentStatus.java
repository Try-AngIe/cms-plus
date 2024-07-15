package kr.or.kosa.cmsplusmain.domain.payment.entity;

import lombok.Getter;

@Getter
public enum PaymentStatus {
	ENABLED("사용"), DISABLED("미사용");

	private final String title;

	PaymentStatus(String title) {
		this.title = title;
	}

	public static PaymentStatus fromTitle(String title) {
		if (title == null) {
			return null;
		}
		for (PaymentStatus status : PaymentStatus.values()) {
			if (title.equalsIgnoreCase(status.title)) {
				return status;
			}
		}
		throw new IllegalArgumentException(title);
	}
}
