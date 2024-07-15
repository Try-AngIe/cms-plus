package kr.or.kosa.cmsplusmain.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

import jakarta.annotation.PostConstruct;

@Component
public class SmsUtil { //util로 뺴도 됨

	@Value("${coolsms.sender-number}")
	private String senderNumber;

	@Value("${coolsms.api-key}")
	private String apiKey;

	@Value("${coolsms.api-secret}")
	private String apiSecret;

	DefaultMessageService messageService;

	@PostConstruct
	public void init() {
		this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
	}

	public SingleMessageSentResponse sendSms(String to, String verificationCode) {
		Message message = new Message();
		message.setFrom(senderNumber);
		message.setTo(to);
		message.setText("[CMS+] 본인 확인 인증번호는 " + verificationCode + "입니다.");

		SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
		System.out.println(response);
		return response;
	}

}