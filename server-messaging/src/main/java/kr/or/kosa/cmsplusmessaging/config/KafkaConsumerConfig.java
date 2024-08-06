 package kr.or.kosa.cmsplusmessaging.config;

 import kr.or.kosa.cmsplusmessaging.dto.MessageDto;
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

     @Value("${kafkaGroup.messagingGroup}")
     private String messagingGroup;

     @Value("${kafkaClient.messagingClient}")
     private String messagingClient;

     @Bean
     public ConcurrentKafkaListenerContainerFactory<String, MessageDto> messagingKafkaListenerContainerFactory() {
         ConcurrentKafkaListenerContainerFactory<String, MessageDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
         factory.setConsumerFactory(messagingConsumerFactory());
         factory.setBatchListener(true);
         factory.setConcurrency(3);
         factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
         factory.getContainerProperties().setIdleBetweenPolls(500);
         return factory;
     }

     @Bean
     public ConsumerFactory<String, MessageDto> messagingConsumerFactory() {
         Map<String, Object> props = new HashMap<>();
         props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerIp);
         props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
         props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
         props.put(ConsumerConfig.GROUP_ID_CONFIG, messagingGroup);
         props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
         props.put(ConsumerConfig.CLIENT_ID_CONFIG, messagingClient);
         props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
         JsonDeserializer<MessageDto> jsonDeserializer = new JsonDeserializer<>(MessageDto.class, false);
         return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
     }

 }
