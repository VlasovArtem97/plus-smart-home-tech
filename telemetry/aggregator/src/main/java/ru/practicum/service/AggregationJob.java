package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.mapper.SensorSnapshotMapper;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AggregationJob {

    private final Map<String, SensorsSnapshotAvro> snapshotAvroMap = new HashMap<>();
    private final SensorSnapshotMapper mapper;

    Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        return Optional.ofNullable(snapshotAvroMap.get(event.getHubId()))
                .filter(oldSensorStateAvro -> oldSensorStateAvro.getTimestamp()
                        .isBefore(event.getTimestamp()))
                .filter(oldSensorStateAvro -> !oldSensorStateAvro.getSensorsState()
                        .equals(event.getPayload()))
                .map(oldSensorStateAvro -> {
                    SensorsSnapshotAvro sensorsSnapshotAvro = mapper.toSensorSnapshotAvro(event);
                    snapshotAvroMap.put(event.getHubId(), sensorsSnapshotAvro);
                    return snapshotAvroMap.get(event.getHubId());
                });
    }

}
