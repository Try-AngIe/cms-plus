 package kr.or.kosa.cmspluspayment.config;

 import kr.or.kosa.cmspluspayment.dto.PaymentResultDto;
 import org.apache.kafka.clients.producer.ProducerConfig;
 import org.apache.kafka.common.serialization.StringSerializer;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.kafka.core.DefaultKafkaProducerFactory;
 import org.springframework.kafka.core.KafkaTemplate;
 import org.springframework.kafka.core.ProducerFactory;
 import org.springframework.kafka.support.serializer.JsonSerializer;

 import java.util.HashMap;
 import java.util.Map;

 @Configuration
 public class KafkaProducerConfig {

     @Value("${kafkaServer.ip}")
     private String kafkaServerIp;

     // 결제결과 메인서버에 발송
     @Bean
     public KafkaTemplate<String, PaymentResultDto> paymentResultkafkaTemplate() {
         return new KafkaTemplate<>(paymentResultproducerFactory());
     }

     @Bean
     public ProducerFactory<String, PaymentResultDto> paymentResultproducerFactory() {
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
