package ru.practicum.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.avrodeserializer.BaseAvroDeserializer;
import ru.practicum.avroserialization.AvroSerialization;

import java.time.Duration;
import java.util.Properties;

@Component
@Slf4j
public class KafkaClientImpl implements KafkaClient, AutoCloseable {

    @Value("${kafka.port}")
    private String kafkaPort;

    @Value("${kafka.consumerClientIdForAggregator}")
    private String clientIdConfig;

    @Value("${kafka.consumerClientIdForAggregator}")
    private String groupId;

    private Producer<String, SpecificRecordBase> producer;

    private Consumer<String, SpecificRecordBase> consumer;

    @Override
    public Producer<String, SpecificRecordBase> getProducer() {
        if (producer == null) {
            initProducer();
        }
        return producer;
    }

    @Override
    public Consumer<String, SpecificRecordBase> getConsumer() {
        if (consumer == null) {
            initConsumer();
        }
        return consumer;
    }

    private void initProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaPort);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerialization.class.getName());
        producer = new org.apache.kafka.clients.producer.KafkaProducer<>(config);
    }

    private void initConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, clientIdConfig);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaPort);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, BaseAvroDeserializer.class.getName());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumer = new KafkaConsumer<>(config);
    }

    @Override
    public void close() {
        try {
            producer.flush();
            if (consumer != null) {
                consumer.commitSync();
            }
        } catch (Exception e) {
            log.error("Ошибка при завершении работы: ", e);
        } finally {
            if (consumer != null) {
                log.info("Закрываем консьюмер");
                consumer.close(Duration.ofSeconds(10));
                consumer = null;
            }
            if (producer != null) {
                log.info("Закрываем продюсер");
                producer.close();
                producer = null;
            }
        }
    }
}