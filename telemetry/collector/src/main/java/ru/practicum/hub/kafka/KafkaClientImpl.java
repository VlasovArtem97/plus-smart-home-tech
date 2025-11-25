package ru.practicum.hub.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.avroserialization.AvroSerialization;

import java.time.Duration;
import java.util.Properties;

@Component
public class KafkaClientImpl implements KafkaClient, AutoCloseable {

    @Value("${kafka.port}")
    private String kafkaPort;

    private Producer<String, SpecificRecordBase> producer;

    @Override
    public Producer<String, SpecificRecordBase> getProducer() {
        if (producer == null) {
            initProducer();
        }
        return producer;
    }

    private void initProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaPort);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerialization.class.getName());
        producer = new KafkaProducer<>(config);
    }

    @Override
    public void close() throws Exception {
        if (producer != null) {
            producer.flush();
            producer.close(Duration.ofSeconds(10));
            producer = null;
        }
    }
}
