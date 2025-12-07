package ru.practicum.hub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.hub.kafka.KafkaClientImpl;
import ru.practicum.hub.mapper.HubMapper;
import ru.practicum.hub.mapper.SensorMapper;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {

    private final KafkaClientImpl kafkaClient;

    @Value("${kafka.topicSensor}")
    private String topicSensor;

    @Value("${kafka.topicHub}")
    private String topicHub;

    private final HubMapper hubMapper;

    private final SensorMapper sensorMapper;

    @Override
    public void addSensors(SensorEventProto sensorEvent) {
        SensorEventAvro sensorEventAvro = sensorMapper.toSensorEventAvroFromSensorEventProto(sensorEvent);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topicSensor, sensorEventAvro);
        log.debug("Отправка сенсорных данных: тип {} в топик {}", sensorEvent.getPayloadCase(), topicSensor);
        sendMessageTopic(record, "SensorEvent");
    }

    @Override
    public void addHubs(HubEventProto hubEvent) {
        HubEventAvro hubEventAvro = hubMapper.toHubEventAvroFromHubEventProto(hubEvent);
        log.info("Объект {}", hubEventAvro);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topicHub, hubEventAvro);
        log.info("Отправка данных хаба: тип {} в топик {}", hubEvent.getPayloadCase(), topicHub);
        sendMessageTopic(record, "hubEvent");
    }

    private void sendMessageTopic(ProducerRecord<String, SpecificRecordBase> record, String typeEvent) {
        try {
            Producer<String, SpecificRecordBase> producer = kafkaClient.getProducer();
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Не удалось отправить {}: {}", typeEvent, exception.getMessage());
                } else {
                    log.info("{} успешно отправлены в партицию {} с смещением {}", typeEvent, metadata.partition(), metadata.offset());
                }
            });
            kafkaClient.close();
        } catch (Exception e) {
            log.error("Ошибка при отправке {}", typeEvent, e);
        }
    }
}
