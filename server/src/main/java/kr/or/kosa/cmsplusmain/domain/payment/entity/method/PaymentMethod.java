package kr.or.kosa.cmsplusmain.domain.payment.entity.method;

import java.util.Arrays;

import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* 결제수단 */
@RequiredArgsConstructor
public enum PaymentMethod implements BaseEnum {
	CARD("카드"),
	CMS("실시간CMS"),
	ACCOUNT("계좌");

	private final String title;

	public static class Const {
		public static final String CARD = "CARD";
		public static final String CMS = "CMS";
		public static final String ACCOUNT = "ACCOUNT";
	}

	@Override
	public String getCode() {
		return name();
	}

	@Override
	public String getTitle() {
		return title;
	}
}