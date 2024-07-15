package kr.or.kosa.cmsplusmain.domain.payment.entity;

import lombok.Getter;

@Getter
public enum Bank {
	SINHAN("088", "신한은행"), KOOKMIN("004", "국민은행");

	private final String code;
	private final String title;

	Bank(String code, String title) {
		this.code = code;
		this.title = title;
	}

	public static Bank fromCode(String code) {
		if (code == null || code.isEmpty()) {
			return null;
		}

		for (Bank bank : values()) {
			if (bank.code.equals(code)) {
				return bank;
			}
		}

		return null;
	}
}
