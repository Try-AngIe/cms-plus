package kr.or.kosa.cmsplusmessaging.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.or.kosa.cmsplusmessaging.dto.EmailMessageDto;
import kr.or.kosa.cmsplusmessaging.dto.MessageDto;
import kr.or.kosa.cmsplusmessaging.dto.SmsMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingService {

    @Value("${sms.phone}")
    private String smsPhone;

    @Value("${email.emailAddress}")
    private String emailAddress;

    private final DefaultMessageService messageService;

    private final JavaMailSender mailSender;

    @KafkaListener(topics = "messaging-topic", groupId = "messaging-group", containerFactory = "messagingKafkaListenerContainerFactory")
    public void consumeMessage(List<ConsumerRecord<String, MessageDto>> consumerRecords) {
        log.error("컨슘 메시징 시작");
        log.error(consumerRecords.toString());
        List<SmsMessageDto> smsMessages = new ArrayList<>();
        List<EmailMessageDto> emailMessages = new ArrayList<>();
        for (ConsumerRecord<String, MessageDto> consumerRecord : consumerRecords) {
            MessageDto messageDto = consumerRecord.value();

            switch (messageDto.getMethod()) {
                case SMS -> smsMessages.add((SmsMessageDto) messageDto);
                case EMAIL -> emailMessages.add((EmailMessageDto) messageDto);
            }
        }
        if (!smsMessages.isEmpty()) sendManySms(smsMessages);
        if (!emailMessages.isEmpty()) sendManyEmails(emailMessages);
    }

    // SMS
    private void sendManySms(List<SmsMessageDto> smsMessages) {
     List<Message> messageList = new ArrayList<>();
     for (SmsMessageDto smsMessageDto : smsMessages) {
         Message message = new Message();
         message.setTo(smsMessageDto.getPhoneNumber());
         message.setFrom(smsPhone);
         message.setText(smsMessageDto.getText());
         messageList.add(message);
     }
     try {
         MultipleDetailMessageSentResponse response = messageService.send(messageList, true);
         log.error("[SMS 발송 성공]");
         } catch (NurigoMessageNotReceivedException e) {
             log.error(e.getFailedMessageList().toString(), e);
         } catch (Exception e) {
             log.error(e.getMessage(), e);
         }
     }

    // Email
    private void sendManyEmails(List<EmailMessageDto> emailMessageDtoList) {
     MimeMessage[] messages = new MimeMessage[emailMessageDtoList.size()];
     for (int i = 0; i < emailMessageDtoList.size(); i++) {
         EmailMessageDto emailMessageDto = emailMessageDtoList.get(i);
         try {
             MimeMessage message = mailSender.createMimeMessage();
             MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
             helper.setTo(emailMessageDto.getEmailAddress());
             helper.setFrom(emailAddress);
             helper.setSubject("보낼 이메일 제목 테스트 123");
             helper.setText(emailMessageDto.getText());
             messages[i] = message;
             log.error("[이메일 발송 성공]");
         } catch (MessagingException | MailException e) {
             log.error("[이메일 발송 실패]: " + emailMessageDto.getEmailAddress(), e);
         }
     }
     mailSender.send(messages);
    }

}
