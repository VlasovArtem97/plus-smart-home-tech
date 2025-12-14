package ru.practicum.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.kafka.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class SnapshotProcessor {

    private final KafkaClient kafkaClient;

    private final SnapshotProcessorJob snapshotProcessorJob;

    private Consumer<String, SpecificRecordBase> consumer;

    @Value("${kafka.topicSnapShot}")
    private String topicSnapshot;

    public SnapshotProcessor(@Qualifier("kafkaClientSnapshotImpl") KafkaClient kafkaClient,
                             SnapshotProcessorJob hubEventProcessorJob) {
        this.kafkaClient = kafkaClient;
        this.snapshotProcessorJob = hubEventProcessorJob;
    }

    public void run() {
        log.info("Начинается работа по получению данных из топика: {} kafka", topicSnapshot);

        try {
            consumer = kafkaClient.getConsumer();

            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            consumer.subscribe(List.of(topicSnapshot));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> record = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, SpecificRecordBase> rec : record) {
                    snapshotProcessorJob.start((SensorsSnapshotAvro) rec.value());
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            log.debug("Начинается закрытие consumer");
            kafkaClient.close();
        }
    }
}
