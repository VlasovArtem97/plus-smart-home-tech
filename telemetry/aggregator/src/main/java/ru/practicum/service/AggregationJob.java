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
        String hubId = event.getHubId();
        SensorsSnapshotAvro existingSnapshot = snapshotAvroMap.get(hubId);
        if (existingSnapshot == null) {
            SensorsSnapshotAvro newSnapshot = mapper.toSensorSnapshotAvro(event);
            snapshotAvroMap.put(hubId, newSnapshot);
            return Optional.of(newSnapshot);
        } else {
            if (existingSnapshot.getTimestamp().isBefore(event.getTimestamp()) &&
                    !existingSnapshot.getSensorsState().equals(event.getPayload())) {
                SensorsSnapshotAvro newSnapshot = mapper.toSensorSnapshotAvro(event);
                snapshotAvroMap.put(hubId, newSnapshot);
                return Optional.of(newSnapshot);
            } else {
                return Optional.empty();
            }
        }
    }

}
