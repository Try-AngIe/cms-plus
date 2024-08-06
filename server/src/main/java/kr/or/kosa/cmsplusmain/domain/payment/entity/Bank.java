package kr.or.kosa.cmsplusmain.domain.payment.entity;

import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Bank implements BaseEnum {
	SHINHAN("088", "신한은행"),
	KOOKMIN("004", "국민은행"),
	WOORI("020", "우리은행"),
	HANA("081", "하나은행"),
	IBK("003", "기업은행"),
	KBANK("089", "케이뱅크"),
	KAKAO("090", "카카오뱅크"),
	NH("011", "농협은행"),
	CITI("027", "씨티은행"),
	SC("023", "SC제일은행"),
	POST("071", "우체국"),
	SAEMAUL("045", "새마을금고"),
	SH("007", "수협은행");

	private final String code;
	private final String title;

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
