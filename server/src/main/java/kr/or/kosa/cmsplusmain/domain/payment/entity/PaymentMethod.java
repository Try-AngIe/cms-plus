package kr.or.kosa.cmsplusmain.domain.payment.entity;

import java.util.Arrays;

import lombok.Getter;

/* 결제수단 */
@Getter
public enum PaymentMethod {
	CARD("카드", Values.CARD),
	CMS("실시간CMS", Values.CMS),
	ACCOUNT("계좌", Values.ACCOUNT);

	private final String title;
	private final String name;

	PaymentMethod(String title, String name) {
		this.title = title;
		this.name = name;
	}

	public static PaymentMethod fromName(String name) {
		if (name == null || name.isBlank()) {
			return null;
		}
		return Arrays.stream(PaymentMethod.values())
			.filter(p -> name.equals(p.name))
			.findAny()
			.orElseThrow();
	}

	/* DiscriminatorValue(PaymentType.Values...)에 사용됨 */
	public static class Values {
		public static final String CARD = "CARD";
		public static final String CMS = "CMS";
		public static final String ACCOUNT = "ACCOUNT";
	}

}
