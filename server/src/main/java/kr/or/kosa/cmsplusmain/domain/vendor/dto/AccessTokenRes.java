package kr.or.kosa.cmsplusmain.domain.vendor.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccessTokenRes {
	private final String accessToken;
	private final String refreshToken;
	private final String role;
}
