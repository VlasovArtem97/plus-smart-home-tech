package ru.practicum.hub.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface SensorMapper {

    @Mapping(target = "payload", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    SensorEventAvro toSensorEventAvroFromSensorEventProto(SensorEventProto event);

    @AfterMapping
    default void getSensorEventAvro(@MappingTarget SensorEventAvro.Builder builder, SensorEventProto event) {
        switch (event.getPayloadCase()) {
            case SWITCH_SENSOR -> builder.setPayload(mapSwitchSensorProtoToAvro(event.getSwitchSensor()));
            case CLIMATE_SENSOR -> builder.setPayload(mapClimateSensorProtoToAvro(event.getClimateSensor()));
            case LIGHT_SENSOR -> builder.setPayload(mapLightSensorProtoToAvro(event.getLightSensor()));
            case MOTION_SENSOR -> builder.setPayload(mapMotionSensorProtoToAvro(event.getMotionSensor()));
            case TEMPERATURE_SENSOR ->
                    builder.setPayload(mapTemperatureSensorProtoToAvro(event.getTemperatureSensor()));
            default -> {
                throw new IllegalStateException("Неизвестный тип: " + event.getPayloadCase());
            }
        }
        builder.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()));
    }

    MotionSensorAvro mapMotionSensorProtoToAvro(MotionSensorProto proto);

    SwitchSensorAvro mapSwitchSensorProtoToAvro(SwitchSensorProto proto);

    ClimateSensorAvro mapClimateSensorProtoToAvro(ClimateSensorProto proto);

    LightSensorAvro mapLightSensorProtoToAvro(LightSensorProto proto);

    TemperatureSensorAvro mapTemperatureSensorProtoToAvro(TemperatureSensorProto proto);
}
