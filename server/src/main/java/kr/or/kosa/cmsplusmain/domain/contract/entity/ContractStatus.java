package kr.or.kosa.cmsplusmain.domain.contract.entity;

import lombok.Getter;

@Getter
public enum ContractStatus {
	ENABLED("사용"), DISABLED("중지");

	private final String title;

	ContractStatus(String title) {
		this.title = title;
	}
}
