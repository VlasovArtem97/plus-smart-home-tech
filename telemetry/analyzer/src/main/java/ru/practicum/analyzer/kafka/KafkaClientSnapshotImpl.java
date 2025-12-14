package ru.practicum.analyzer.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.deserializer.SnapshotAvroDeserializer;

import java.time.Duration;
import java.util.Properties;

@Component("kafkaClientSnapshotImpl")
@Slf4j
public class KafkaClientSnapshotImpl implements KafkaClient, AutoCloseable {

    @Value("${kafka.port}")
    private String kafkaPort;

    @Value("${kafka.consumerClientIdForAnalyzerSnapshot}")
    private String clientIdConfig;

    @Value("${kafka.consumerGroupIdForAnalyzer}")
    private String groupId;

    private Consumer<String, SpecificRecordBase> consumer;

    @Override
    public Consumer<String, SpecificRecordBase> getConsumer() {
        if (consumer == null) {
            initConsumer();
        }
        return consumer;
    }

    private void initConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, clientIdConfig);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaPort);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SnapshotAvroDeserializer.class.getName());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumer = new KafkaConsumer<>(config);
    }

    @Override
    public void close() {
        try {
            if (consumer != null) {
                consumer.close(Duration.ofSeconds(10));
                log.info("Консьюмер закрыт");
            }
        } catch (Exception e) {
            log.error("Ошибка при закрытии консьюмера", e);
        } finally {
            consumer = null;
        }
    }
}
