package kr.or.kosa.cmsplusmain.domain.messaging.dto;

import lombok.Getter;

@Getter
public class SmsVerifyDto {

	private String phone;
	private String smsVerifyCode;

}