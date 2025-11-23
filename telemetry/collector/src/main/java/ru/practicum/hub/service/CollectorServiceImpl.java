package ru.practicum.hub.service;

import jakarta.annotation.PostConstruct;
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
import ru.practicum.hub.model.hubs.*;
import ru.practicum.hub.model.sensors.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {

    private final KafkaClientImpl kafkaClient;

    private Producer<String, SpecificRecordBase> producer;

    @Value("${kafka.topicSensor}")
    private String topicSensor;

    @Value("${kafka.topicHub}")
    private String topicHub;

    private final HubMapper hubMapper;

    private final SensorMapper sensorMapper;

    @PostConstruct
    public void init() {
        this.producer = kafkaClient.getProducer();
        log.info("Kafka Producer инициализирован");
    }

    @Override
    public void addSensors(SensorEvent sensorEvent) {

        ProducerRecord<String, SpecificRecordBase> record = null;

        switch (sensorEvent.getType()) {
            case SWITCH_SENSOR_EVENT -> record = new ProducerRecord<>(topicSensor,
                    sensorMapper.toSensorEventAvroFromSwitchSensorEvent((SwitchSensorEvent) sensorEvent));
            case CLIMATE_SENSOR_EVENT -> record = new ProducerRecord<>(topicSensor,
                    sensorMapper.toSensorEventAvroFromClimateSensorEvent((ClimateSensorEvent) sensorEvent));
            case LIGHT_SENSOR_EVENT -> record = new ProducerRecord<>(topicSensor,
                    sensorMapper.toSensorEventAvroFromLightSensorEvent((LightSensorEvent) sensorEvent));
            case MOTION_SENSOR_EVENT -> record = new ProducerRecord<>(topicSensor,
                    sensorMapper.toSensorEventAvroFromMotionSensorEvent((MotionSensorEvent) sensorEvent));
            case TEMPERATURE_SENSOR_EVENT -> record = new ProducerRecord<>(topicSensor,
                    sensorMapper.toSensorEventAvroFromTemperatureSensorEvent((TemperatureSensorEvent) sensorEvent));
        }
        if (record != null) {
            log.info("Отправка сенсорных данных: тип {} в топик {}", sensorEvent.getType(), topicSensor);
            sendMessageTopic(record, "SensorEvent");
        }
    }

    @Override
    public void addHubs(HubEvent hubEvent) {

        ProducerRecord<String, SpecificRecordBase> record = null;

        switch (hubEvent.getType()) {
            case DEVICE_ADDED -> record = new ProducerRecord<>(topicHub,
                    hubMapper.toHubEventAvroFromDeviceAddedEvent((DeviceAddedEvent) hubEvent));
            case DEVICE_REMOVED -> record = new ProducerRecord<>(topicHub,
                    hubMapper.toHubEventAvroFromDeviceRemovedEvent((DeviceRemovedEvent) hubEvent));
            case SCENARIO_ADDED -> record = new ProducerRecord<>(topicHub,
                    hubMapper.toHubEventAvroFromScenarioAddedEvent((ScenarioAddedEvent) hubEvent));
            case SCENARIO_REMOVED -> record = new ProducerRecord<>(topicHub,
                    hubMapper.toHubEventAvroFromScenarioRemovedEvent((ScenarioRemovedEvent) hubEvent));
        }
        if (record != null) {
            log.info("Отправка данных хаба: тип {} в топик {}", hubEvent.getType(), topicHub);
            sendMessageTopic(record, "hubEvent");
        }
    }

    private void sendMessageTopic(ProducerRecord<String, SpecificRecordBase> record, String typeEvent) {
        try {
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Не удалось отправить {}: {}", typeEvent, exception.getMessage());
                } else {
                    log.info("{} успешно отправлены в партицию {} с смещением {}", typeEvent, metadata.partition(), metadata.offset());
                }
            });
        } catch (Exception e) {
            log.error("Ошибка при отправке {}", typeEvent, e);
        }
    }
}
