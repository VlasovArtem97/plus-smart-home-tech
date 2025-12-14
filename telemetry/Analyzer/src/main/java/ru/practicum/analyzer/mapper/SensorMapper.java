package ru.practicum.analyzer.mapper;

import org.mapstruct.Mapper;
import ru.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;

@Mapper(componentModel = "spring")
public interface SensorMapper {

    Sensor toSensor(DeviceAddedEventAvro avro, String hubId);
}
