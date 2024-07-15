package kr.or.kosa.cmsplusmain.domain.messaging;

import lombok.Getter;

@Getter
public enum MessageSendMethod {
	SMS("SMS"), EMAIL("EMAIL");

	private final String title;

	MessageSendMethod(String title) {
		this.title = title;
	}
}
