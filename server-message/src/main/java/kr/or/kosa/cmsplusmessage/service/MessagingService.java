package kr.or.kosa.cmsplusmessage.service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.or.kosa.cmsplusmessage.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MessagingService {

    @Value("${sms.key}")
    private String smsKey;
    @Value("${sms.secret}")
    private String smsSecret;
    @Value("${sms.domain}")
    private String smsDomain;
    @Value("${sms.phone}")
    private String smsPhone;

    private DefaultMessageService messageService; // coolsms

    // 의존성 주입 후 초기화 - bean이 여러번 초기화되는 것을 방지
    @PostConstruct
    public void init() { // 최초에 쿨에스엠에스 서비스에 내 정보를 매핑시켜야함
        this.messageService = new DefaultMessageService(smsKey, smsSecret, smsDomain);
    }

    // 컨트롤러에서 호출할 메서드
//    public void sendSms(List<MessageDto> messages) {
//        ArrayList<Message> messageList = new ArrayList<>();
//
//        for (MessageDto messageDto : messages) {
//            Message message = new Message();
//            message.setFrom(smsPhone); // 발신번호(내 번호)
//            message.setTo(messageDto.getPhoneNumber()); // 수신번호
//            message.setText(messageDto.getText()); // 수신내용
//            messageList.add(message);
//        }
//
//        sendMany(messageList);
//    }





    // 대량 sms용 메서드(쿨에스엠에스에서 제공)
    private MultipleDetailMessageSentResponse sendMany(ArrayList<Message> messageList) {
        try {
            MultipleDetailMessageSentResponse response = this.messageService.send(messageList, true);
            return response;
        } catch (NurigoMessageNotReceivedException e) {
            log.error(e.getFailedMessageList().toString());
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }








    @Value("${email.emailAddress}")
    private String emailAddress;

    // 이메일 대량은 한 번에 100건, 하루 500건
    private final JavaMailSender mailSender;

    public String sendEmail(MessageDto messageDto) throws MailException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setTo(messageDto.getEmailAddress()); // 메일 보낼 곳
            helper.setFrom(emailAddress); // 메일 보내는 곳
            helper.setSubject("보낼 이메일 제목"); // 메일 제목
            helper.setText(messageDto.getText()); // 메일 내용
            mailSender.send(message); //
            return "[email 전송 성공]";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "[email 전송 실패]";
        }

    }

    public SingleMessageSentResponse sendSms(MessageDto messageDto) {

            Message message = new Message();
            message.setFrom(smsPhone);
            message.setTo(messageDto.getPhoneNumber());
            message.setText(messageDto.getText());
            SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));

            return response;



    }








    private MessageDto messageDto;

    @KafkaListener(topics = "message-topic",groupId = "message-group",containerFactory = "kafkaListenerContainerFactory")
    public void consumeMessage(ConsumerRecord<String, MessageDto> consumerRecord) {
        messageDto = consumerRecord.value();
        if ("sms".equals(messageDto.getType())) { // TYPE이 sms일 때 sms로직으로 가게
            handleSmsMessage(messageDto);
        } else if ("email".equals(messageDto.getType())) { // TYPE이 email일 때 email로직으로 가게
            handleEmailMessage(messageDto);
        }

    }



    private void handleSmsMessage(MessageDto messageDto) {
        log.error("[SMS 메시지 소비됨]: {}", messageDto.toString());
       sendSms(messageDto);
    }

    private void handleEmailMessage(MessageDto messageDto) {
        log.error("[EMAIL 메시지 소비됨]: {}", messageDto.toString());
        sendEmail(messageDto);
    }

}