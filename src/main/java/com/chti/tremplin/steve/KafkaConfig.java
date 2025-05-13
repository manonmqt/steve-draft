package com.chti.tremplin.steve;

import com.chti.tremplin.steve.location.Location;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
@Getter
public class KafkaConfig {


    private static final boolean ENABLE_AUTO_COMMIT_CONFIG_VALUE = true;

    @Value("${kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${kafka.consumer.group-id}")
    private String groupId;

    @Value("${kafka.consumer.max.poll.interval:900000}")
    private Integer pollingTimeout;

    @Value("${kafka.consumer.max.poll.records:20}")
    private Integer maxPollRecords;

    @Value("${kafka.consumer.request.timeout:900000}")
    private Integer requestTimeout;

    @Value("${kafka.consumer.session.timeout:600000}")
    private Integer sessionTimeout;

    @Value("${kafka.consumer.heartbeat.interval:600000}")
    private Integer heartbeatInterval;

    @Value("${kafka.consumer.fetch-max-bytes}")
    private int fetchMaxBytes;

    @Value("${kafka.consumer.fetch-min-bytes}")
    private int fetchMinBytes;

    private Map<String, Object> getConsumerConfig() {
        Map<String, Object> consumerConfigs = new HashMap<>();

        consumerConfigs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        consumerConfigs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, ENABLE_AUTO_COMMIT_CONFIG_VALUE);
        consumerConfigs.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, this.getRequestTimeout());
        consumerConfigs.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, this.getHeartbeatInterval());
        consumerConfigs.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, this.getPollingTimeout());
        consumerConfigs.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, this.getMaxPollRecords());
        consumerConfigs.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, this.getSessionTimeout());
        consumerConfigs.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, this.fetchMaxBytes);
        consumerConfigs.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, this.fetchMinBytes);
        return consumerConfigs;
    }

    @Bean
    public ConsumerFactory<String, Location> consumerFactory() {
        Map<String, Object> configs = this.getConsumerConfig();

        return new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), new JsonDeserializer<>(Location.class));
    }

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Location>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Location> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
