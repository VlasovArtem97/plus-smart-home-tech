package ru.practicum.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface SensorSnapshotMapper {

    @Mapping(target = "sensorsState", ignore = true)
    SensorsSnapshotAvro toSensorSnapshotAvro(SensorEventAvro avro);

    @AfterMapping
    default void toSensorsState(@MappingTarget SensorsSnapshotAvro.Builder snapshot, SensorEventAvro sensorEventAvro) {
        Map<String, SensorStateAvro> map = new HashMap<>();
        map.put(sensorEventAvro.getId(), toSensorStateAvro(sensorEventAvro));
        snapshot.setSensorsState(map);
    }

    @Mapping(source = "payload", target = "data")
    SensorStateAvro toSensorStateAvro(SensorEventAvro avro);

}
