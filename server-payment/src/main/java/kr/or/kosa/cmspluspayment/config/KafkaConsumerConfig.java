 package kr.or.kosa.cmspluspayment.config;

 import kr.or.kosa.cmspluspayment.dto.PaymentDto;
 import org.apache.kafka.clients.consumer.ConsumerConfig;
 import org.apache.kafka.common.serialization.StringDeserializer;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
 import org.springframework.kafka.core.ConsumerFactory;
 import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
 import org.springframework.kafka.listener.ContainerProperties;
 import org.springframework.kafka.support.serializer.JsonDeserializer;
 import java.util.HashMap;
 import java.util.Map;

 @Configuration
 public class KafkaConsumerConfig {

     @Value("${kafkaServer.ip}")
     private String kafkaServerIp;

     @Value("${kafkaGroup.paymentGroup}")
     private String paymentGroup;

     @Value("${kafkaClient.paymentClient}")
     private String paymentClient;

     // 결제데이터 메인서버에서 받음
     @Bean
     public ConcurrentKafkaListenerContainerFactory<String, PaymentDto> paymentKafkaListenerContainerFactory() {
         ConcurrentKafkaListenerContainerFactory<String, PaymentDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
         factory.setConsumerFactory(paymentConsumerFactory());
         factory.setBatchListener(true);
         factory.setConcurrency(3);
         factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
         factory.getContainerProperties().setIdleBetweenPolls(500);
         return factory;
     }

     @Bean
     public ConsumerFactory<String, PaymentDto> paymentConsumerFactory() {
         Map<String, Object> props = new HashMap<>();
         props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerIp);
         props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
         props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
         props.put(ConsumerConfig.GROUP_ID_CONFIG, paymentGroup);
         props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
         props.put(ConsumerConfig.CLIENT_ID_CONFIG, paymentClient);
         props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
         JsonDeserializer<PaymentDto> jsonDeserializer = new JsonDeserializer<>(PaymentDto.class, false);
         return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
     }

 }