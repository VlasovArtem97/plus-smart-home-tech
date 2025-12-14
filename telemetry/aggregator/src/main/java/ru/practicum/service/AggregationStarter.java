package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final KafkaClient kafkaClient;

    private final AggregationJob aggregationJob;

    private Consumer<String, SpecificRecordBase> consumer;

    private Producer<String, SpecificRecordBase> producer;

    @Value("${kafka.topicSensor}")
    private String topicSensor;

    @Value("${kafka.topicSnapShot}")
    private String topicSnapShot;

    public void start() {

        log.info("Начинается работа по получению данных из топика: {} kafka", topicSensor);

        try {
            consumer = kafkaClient.getConsumer();
            producer = kafkaClient.getProducer();

            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            consumer.subscribe(List.of(topicSensor));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> record = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, SpecificRecordBase> rec : record) {
                    record(rec);
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            log.debug("Начинается закрытие producer и consumer");
            kafkaClient.close();
        }
    }

    private void record(ConsumerRecord<String, SpecificRecordBase> rec) {
        log.debug("Получены данные: {} из топика: {} kafka", rec.value(), topicSensor);
        Optional<SensorsSnapshotAvro> sensorsSnapshotAvro = aggregationJob
                .updateState((SensorEventAvro) rec.value());
        if (sensorsSnapshotAvro.isPresent()) {
            SensorsSnapshotAvro snapshotAvro = sensorsSnapshotAvro.get();
            log.debug("Данные SensorsSnapshotAvro обновлены: {}", snapshotAvro);
            sendMessageTopic(new ProducerRecord<>(topicSnapShot, snapshotAvro), "SnapshotEvent");
        } else {
            log.debug("Данные не были обновлены");
        }
    }

    private void sendMessageTopic(ProducerRecord<String, SpecificRecordBase> record, String typeEvent) {
        try {
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Не удалось отправить {}: {}", typeEvent, exception.getMessage());
                } else {
                    log.debug("{} успешно отправлены в партицию {} с смещением {}", typeEvent, metadata.partition(), metadata.offset());
                }
            });
        } catch (Exception e) {
            log.error("Ошибка при отправке {}", typeEvent, e);
        }
    }

}
