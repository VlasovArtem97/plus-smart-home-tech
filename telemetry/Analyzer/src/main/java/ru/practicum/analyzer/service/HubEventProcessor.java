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
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class HubEventProcessor implements Runnable {

    private final KafkaClient kafkaClient;

    private final HubEventProcessorJob hubEventProcessorJob;

    private Consumer<String, SpecificRecordBase> consumer;

    @Value("${kafka.topicHub}")
    private String topicHubEvent;

    public HubEventProcessor(@Qualifier("kafkaClientHubEventImpl") KafkaClient kafkaClient,
                             HubEventProcessorJob hubEventProcessorJob) {
        this.kafkaClient = kafkaClient;
        this.hubEventProcessorJob = hubEventProcessorJob;
    }

    @Override
    public void run() {
        log.info("Начинается работа по получению данных из топика: {} kafka", topicHubEvent);

        try {
            consumer = kafkaClient.getConsumer();

            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            consumer.subscribe(List.of(topicHubEvent));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> record = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, SpecificRecordBase> rec : record) {
                    hubEventProcessorJob.start((HubEventAvro) rec.value());
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
