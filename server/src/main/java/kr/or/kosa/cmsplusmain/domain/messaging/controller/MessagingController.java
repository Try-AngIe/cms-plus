package kr.or.kosa.cmsplusmain.domain.messaging.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.kosa.cmsplusmain.domain.messaging.dto.SmsVerifyDto;
import kr.or.kosa.cmsplusmain.domain.messaging.service.SmsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vendor/auth")
public class MessagingController {

	private final SmsService smsService;

	// SMS 인증번호 요청
	@PostMapping("/sms/sending")
	public ResponseEntity<String> sendSms(@RequestBody SmsVerifyDto smsVerifyDto) {
		try {
			smsService.sendSmsCode(smsVerifyDto);
			return ResponseEntity.ok("[SMS] 인증코드 전송 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// SMS 인증번호 검사
	@PostMapping("/sms/verification")
	public ResponseEntity<String> SmsVerification(@RequestBody SmsVerifyDto smsVerifyDto) {
		try {
			smsService.verifySmsCode(smsVerifyDto);
			return ResponseEntity.ok("[SMS] 인증 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

}
