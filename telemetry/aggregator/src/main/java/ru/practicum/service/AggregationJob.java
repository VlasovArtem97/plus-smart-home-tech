package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.mapper.SensorSnapshotMapper;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
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
        SensorsSnapshotAvro existingSnapshot = snapshotAvroMap.get(hubId);
        if (existingSnapshot == null) {
            log.debug("Данных SensorsSnapshotAvro по hubId: {}, не обнаружено", event.getHubId());
            SensorsSnapshotAvro newSnapshot = mapper.toSensorSnapshotAvro(event);
            snapshotAvroMap.put(hubId, newSnapshot);
            return Optional.of(newSnapshot);
        } else {
            log.debug("Найдены старые данные SensorsSnapshotAvro по hubId: {}, {}", event.getHubId(), existingSnapshot);
            if (existingSnapshot.getTimestamp().isBefore(event.getTimestamp()) &&
                    !existingSnapshot.getSensorsState().equals(event.getPayload())) {
                SensorsSnapshotAvro newSnapshot = mapper.toSensorSnapshotAvro(event);
                snapshotAvroMap.put(hubId, newSnapshot);
                log.debug("Обновленный объект: {}", newSnapshot);
                return Optional.of(newSnapshot);
            } else {
                log.debug("Указанные данные в полученном объекте не соответствуют обновленным " +
                        "данным (время старое, либо тот-же объект передан). Переданный объект: {}. Сохраненный объект: " +
                        "{}", event, existingSnapshot);
                return Optional.empty();
            }
        }
    }

}
