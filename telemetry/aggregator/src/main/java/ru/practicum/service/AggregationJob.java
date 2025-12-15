package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.mapper.SensorSnapshotMapper;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationJob {

    private final Map<String, SensorsSnapshotAvro> snapshotAvroMap = new HashMap<>();
    private final SensorSnapshotMapper mapper;

    Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        log.info("Начинается процесс обновления данных по полученному объекту: {}", event);

        String hubId = event.getHubId();
        String sensorId = event.getId();

        SensorsSnapshotAvro oldSnapshot = snapshotAvroMap.get(hubId);
        if (oldSnapshot == null) {
            log.debug("Данных в SensorsSnapshotAvro по hubId: {}, не обнаружено", event.getHubId());
            SensorsSnapshotAvro newSnapshot = mapper.toSensorSnapshotAvro(event);
            snapshotAvroMap.put(hubId, newSnapshot);
            return Optional.of(newSnapshot);
        }

        SensorStateAvro oldSensorState = oldSnapshot.getSensorsState().get(sensorId);
        if (oldSensorState == null) {
            log.debug("Данных в SensorStateAvro по sensorId: {} из SensorsSnapshotAvro c hubId: {}, не обнаружено",
                    sensorId, hubId);
            SensorStateAvro newSensorState = mapper.toSensorStateAvro(event);
            oldSnapshot.getSensorsState().put(sensorId, newSensorState);
            snapshotAvroMap.put(hubId, oldSnapshot);
            return Optional.of(snapshotAvroMap.get(hubId));
        }

        if (oldSensorState.getTimestamp().isAfter(event.getTimestamp())) {
            log.debug("Timestamp у переданного датчика стоит раньше, чем у сохраненного датчика. Время переданного " +
                            "датчика: {}, Время сохранного датчика: {}. Обновление не требуется", oldSnapshot.getTimestamp(),
                    event.getTimestamp());
            return Optional.empty();
        } else {
            if (oldSensorState.getData().equals(event.getPayload())) {
                log.debug("Переданный объект {}, равен ранее сохраненному: {}. Обновление не требуется",
                        oldSensorState.getData(), event);
            } else {
                SensorStateAvro updateSensorStateAvro = mapper.toSensorStateAvro(event);
                oldSnapshot.getSensorsState().put(sensorId, updateSensorStateAvro);
                snapshotAvroMap.put(hubId, oldSnapshot);
                return Optional.of(snapshotAvroMap.get(hubId));
            }
        }
        return Optional.empty();
    }

}
