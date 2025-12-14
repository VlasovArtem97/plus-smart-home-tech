package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.mapper.ScenarioMapper;
import ru.practicum.analyzer.mapper.SensorMapper;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.model.Sensor;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class HubEventProcessorJob {

    private final SensorRepository sensorRepository;
    private final SensorMapper sensorMapper;
    private final ScenarioRepository scenarioRepository;
    private final ScenarioMapper scenarioMapper;

    public void start(HubEventAvro eventAvro) {
        log.info("Получен объект типа HubEventAvro: {}", eventAvro);

        String hubId = eventAvro.getHubId();
        switch (eventAvro.getPayload()) {
            case DeviceAddedEventAvro da -> addDevice(da, hubId);
            case DeviceRemovedEventAvro dr -> removeDevice(dr, hubId);
            case ScenarioAddedEventAvro sa -> addScenario(sa, hubId);
            case ScenarioRemovedEventAvro sr -> removeScenario(sr, hubId);
            default -> throw new IllegalArgumentException("Передана неизвестная инструкция " +
                    eventAvro.getPayload().getClass().getSimpleName());
        }
    }

    public void addDevice(DeviceAddedEventAvro eventAvro, String hubId) {
        log.info("Начинается процесс добавления датчика: {}", eventAvro);

        String sensorId = eventAvro.getId();

        Optional<Sensor> sensor = sensorRepository.findByIdAndHubId(sensorId, hubId);
        if (sensor.isEmpty()) {
            log.info("Датчик с id: {}, из hubId: {} уже добавлен", sensorId, hubId);
            return;
        }
        Sensor addSensor = sensorRepository.save(sensorMapper.toSensor(eventAvro, hubId));
        log.debug("Датчик успешно добавлен {}", addSensor);

    }

    public void removeDevice(DeviceRemovedEventAvro eventAvro, String hubId) {
        log.info("Начинается процесс удаления датчика: {}", eventAvro);

        String sensorId = eventAvro.getId();

        Optional<Sensor> sensor = sensorRepository.findByIdAndHubId(sensorId, hubId);
        if (sensor.isEmpty()) {
            log.info("Датчик с id: {}, из hubId: {} не существует", sensorId, hubId);
            return;
        }
        sensorRepository.deleteById(sensorId);
        log.debug("Датчик c id {} успешно удален", sensorId);
    }

    public void addScenario(ScenarioAddedEventAvro avro, String hubId) {
        log.info("Начинается процесс добавления сценария: {}", avro);

        Set<String> sensorIds = Stream.concat(
                        avro.getConditions().stream()
                                .map(ScenarioConditionAvro::getSensorId),
                        avro.getActions().stream()
                                .map(DeviceActionAvro::getSensorId)
                )
                .collect(Collectors.toSet());

        boolean sensors = sensorRepository.existsByIdInAndHubId(sensorIds, hubId);
        if (!sensors) {
            log.error("В hub c id {}, отсутствую сенсоры с Ids: {}", hubId, sensorIds);
            return;
//            throw new IllegalArgumentException("В hub c Id " + hubId + " отсутствуют датчики с Ids: " + sensorIds);
        }

        Optional<Scenario> scenario = scenarioRepository.findByHubIdAndName(hubId, avro.getName());

        Scenario newScenario;
        if (scenario.isPresent()) {
            log.info("Сценарий с hubId: {} и name: {} существует. Начинаем обновлять сценарий", hubId, avro.getName());
            newScenario = scenario.get();
            scenarioMapper.updateScenario(newScenario, avro);
            log.debug("Обновленный объект Scenario: {}", newScenario);

        } else {
            log.info("Создается новый сценарий");
            newScenario = scenarioMapper.toScenario(avro, hubId);
            log.debug("Новый объект Scenario: {}", newScenario);
        }
        Scenario scenario1 = scenarioRepository.save(newScenario);
        log.debug("Сохраненный объект из базы данных: {}", scenario1);
    }

    public void removeScenario(ScenarioRemovedEventAvro avro, String hubId) {
        log.info("Начинается процесс удаления сценария сценария: {}", avro);

        Optional<Scenario> scenario = scenarioRepository.findByHubIdAndName(hubId, avro.getName());
        if (scenario.isPresent()) {
            scenarioRepository.deleteById(scenario.get().getId());
            log.debug("Сценарий успешно удален");
        } else {
            log.warn("Сценарий с именем: {} и hubId: {} не найден. Удаление не произошло", avro.getName(), hubId);
        }
    }
}
