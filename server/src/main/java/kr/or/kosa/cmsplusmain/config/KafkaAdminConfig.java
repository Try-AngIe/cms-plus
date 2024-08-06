package kr.or.kosa.cmsplusmain.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class KafkaAdminConfig {

    /*
    * Kafka Broker 설정이 auto.create.topics.enable면
    * TopicConfig에서 서버 올라갈 때 topic을 생성하지 않아도
    * KafkaTemplate을 통헤 produce할 때 없는 topic이더라도 생성하면서 produce함
    * */
    @Value("${kafkaServer.ip}")
    private String kafkaServerIp;

    @Value("${kafkaTopic.messagingTopic}")
    private String messagingTopic;

    @Value("${kafkaTopic.paymentTopic}")
    private String paymentTopic;

    // 카프카 어드민 설정
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerIp);
        return new KafkaAdmin(configs);
    }

    @Bean
    public KafkaAdmin.NewTopics newTopics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(messagingTopic)
                        .partitions(3)
                        .replicas(3)
                        .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "2") // 동기화 레플리카 수
                        .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(60000))  // 메시지 보관 시간
                        .build(),
                TopicBuilder.name(paymentTopic)
                        .partitions(3)
                        .replicas(2)
                        .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "2")
                        .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(60000))
                        .build()
        );
    }

}
