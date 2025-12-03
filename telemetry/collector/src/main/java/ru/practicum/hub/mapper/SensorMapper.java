package ru.practicum.hub.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface SensorMapper {

    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "id", target = "id")
    @Mapping(target = "payload", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    SensorEventAvro toSensorEventAvroFromSensorEventProto(SensorEventProto event);

    @AfterMapping
    default void getSensorEventAvro(@MappingTarget SensorEventAvro.Builder builder, SensorEventProto event) {
        switch (event.getPayloadCase()) {
            case SWITCH_SENSOR -> builder.setPayload(event.getSwitchSensor());
            case CLIMATE_SENSOR -> builder.setPayload(event.getClimateSensor());
            case LIGHT_SENSOR -> builder.setPayload(event.getLightSensor());
            case MOTION_SENSOR -> builder.setPayload(event.getMotionSensor());
            case TEMPERATURE_SENSOR -> builder.setPayload(event.getTemperatureSensor());
            default -> {
                throw new IllegalStateException("Неизвестный тип: " + event.getPayloadCase());
            }
        }
        builder.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()));
    }
}
