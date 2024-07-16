package kr.or.kosa.cmsplusmain.domain.messaging.service;

import kr.or.kosa.cmsplusmain.domain.messaging.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingService {


	private final KafkaTemplate<String, MessageDto> kafkaTemplate;

	public void send(String topic, MessageDto messageDto) {
		log.error("[토픽]={} [페이로드]={}", topic, messageDto);
		kafkaTemplate.send(topic, messageDto);
	}

	public void sendEmailMessage(String topic, MessageDto messageDto) {
		log.error("[토픽]={} [페이로드]={}", topic, messageDto);
		kafkaTemplate.send(topic, messageDto);
	}


	public void sendSms(String phone, String message) {
		log.info("SMS 발송: {}\n{}",  phone, message);
	}

	public void sendEmail(String email, String message) {
		log.info("EMAIL 발송: {}\n{}", email, message);
	}
}