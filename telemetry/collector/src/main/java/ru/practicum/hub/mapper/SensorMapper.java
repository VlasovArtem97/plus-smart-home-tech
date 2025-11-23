package ru.practicum.hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.hub.model.sensors.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Mapper(componentModel = "spring")
public interface SensorMapper {

    @Named("climateSensorEventToAvro")
    ClimateSensorAvro toClimateSensorAvro(ClimateSensorEvent event);

    @Named("lightSensorEventToAvro")
    LightSensorAvro toLightSensorAvro(LightSensorEvent event);

    @Named("motionSensorEventToAvro")
    MotionSensorAvro toMotionSensorAvro(MotionSensorEvent event);

    @Named("switchSensorEventToAvro")
    SwitchSensorAvro toSwitchSensorAvro(SwitchSensorEvent event);

    @Named("temperatureSensorEventToAvro")
    TemperatureSensorAvro toTemperatureSensorAvro(TemperatureSensorEvent event);

    @Mapping(source = "event", target = "payload", qualifiedByName = "climateSensorEventToAvro")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "id", target = "id")
    SensorEventAvro toSensorEventAvroFromClimateSensorEvent(ClimateSensorEvent event);

    @Mapping(source = "event", target = "payload", qualifiedByName = "lightSensorEventToAvro")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "id", target = "id")
    SensorEventAvro toSensorEventAvroFromLightSensorEvent(LightSensorEvent event);

    @Mapping(source = "event", target = "payload", qualifiedByName = "motionSensorEventToAvro")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "id", target = "id")
    SensorEventAvro toSensorEventAvroFromMotionSensorEvent(MotionSensorEvent event);

    @Mapping(source = "event", target = "payload", qualifiedByName = "switchSensorEventToAvro")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "id", target = "id")
    SensorEventAvro toSensorEventAvroFromSwitchSensorEvent(SwitchSensorEvent event);

    @Mapping(source = "event", target = "payload", qualifiedByName = "temperatureSensorEventToAvro")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "id", target = "id")
    SensorEventAvro toSensorEventAvroFromTemperatureSensorEvent(TemperatureSensorEvent event);
}
