package kr.or.kosa.cmsplusmain.domain.messaging.service;

import kr.or.kosa.cmsplusmain.domain.messaging.dto.SmsVerifyDto;

public interface SmsService {

	// 인증번호 요청
	public void sendSmsCode(SmsVerifyDto smsVerifyDto);

	// 인증번호 검사
	public void verifySmsCode(SmsVerifyDto smsVerifyDto);

	// 인증번호 검사(boolean-verifySmsCode에서 이용)
	public boolean isVerify(SmsVerifyDto smsVerifyDto);

}
