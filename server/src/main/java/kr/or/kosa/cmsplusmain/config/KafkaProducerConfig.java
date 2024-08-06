package kr.or.kosa.cmsplusmain.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import kr.or.kosa.cmsplusmain.domain.kafka.dto.messaging.MessageDto;
import kr.or.kosa.cmsplusmain.domain.kafka.dto.payment.PaymentDto;

@Configuration
public class KafkaProducerConfig {

    @Value("${kafkaServer.ip}")
    private String kafkaServerIp;

    // SMS. EMAIL 메시징 서버에 보냄
    @Bean
    public KafkaTemplate<String, MessageDto> messagingKafkaTemplate() {
        return new KafkaTemplate<>(messagingProducerFactory());
    }

    @Bean
    public ProducerFactory<String, MessageDto> messagingProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerIp);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // 메시지 Leader가 모든 Replica까지 Commit되면 ack를 보냄(가장 느린 속도 / 가장 높은 보장성) - 정확히 한 번(중복 방지)
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "100000"); // 메시지 전송 실패 시 재전송 대기시간
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, "100000"); // 문자열 기준 500건씩
        props.put(ProducerConfig.LINGER_MS_CONFIG, "500"); // 만약 배치사이즈(500건)이 안 들어왔으면 마냥 대기할 수 없음; 0.5초 후에 보냄
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true"); // 멱등성 설정 - 정확히 한 번(중복 방지)
        props.put(ProducerConfig.RETRIES_CONFIG, "10"); // 멱등성 설정시 값이 0보다 커야함; default 설정은 무한 재시도 - 정확히 한 번(중복 방지)
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5"); // 메시지 중복 방지
        return new DefaultKafkaProducerFactory<>(props);
    }

    // 결제데이터 결제서버에 보냄
    @Bean
    public KafkaTemplate<String, PaymentDto> paymentKafkaTemplate() {
        return new KafkaTemplate<>(paymentProducerFactory());
    }

    @Bean
    public ProducerFactory<String, PaymentDto> paymentProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerIp);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "100000");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, "100000");
        props.put(ProducerConfig.LINGER_MS_CONFIG, "500");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.RETRIES_CONFIG, "10");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        return new DefaultKafkaProducerFactory<>(props);
    }

}
