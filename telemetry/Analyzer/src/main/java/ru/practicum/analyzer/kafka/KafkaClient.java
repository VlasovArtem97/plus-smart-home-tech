package ru.practicum.analyzer.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;

public interface KafkaClient {

    Consumer<String, SpecificRecordBase> getConsumer();

    void close();
}
