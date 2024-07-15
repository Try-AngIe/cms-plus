package kr.or.kosa.cmsplusmain.domain.payment.entity;

import lombok.Getter;

@Getter
public enum ConsentStatus {
	NONE("미동의"), WAIT("대기중"), ACCEPT("승인"), NOT_USED("불필요");

	private final String title;

	ConsentStatus(String title) {
		this.title = title;
	}
}
