package ru.practicum.hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.hub.model.sensors.*;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Mapper(componentModel = "spring")
public interface SensorMapper {

    @Mapping(source = "event", target = "payload")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "id", target = "id")
    SensorEventAvro toSensorEventAvroFromClimateSensorEvent(ClimateSensorEvent event);

    @Mapping(source = "event", target = "payload")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "id", target = "id")
    SensorEventAvro toSensorEventAvroFromLightSensorEvent(LightSensorEvent event);

    @Mapping(source = "event", target = "payload")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "id", target = "id")
    SensorEventAvro toSensorEventAvroFromMotionSensorEvent(MotionSensorEvent event);

    @Mapping(source = "event", target = "payload")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "id", target = "id")
    SensorEventAvro toSensorEventAvroFromSwitchSensorEvent(SwitchSensorEvent event);

    @Mapping(source = "event", target = "payload")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "id", target = "id")
    SensorEventAvro toSensorEventAvroFromTemperatureSensorEvent(TemperatureSensorEvent event);
}
