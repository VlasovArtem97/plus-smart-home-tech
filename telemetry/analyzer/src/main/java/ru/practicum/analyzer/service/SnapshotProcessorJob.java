package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.mapper.ActionMapper;
import ru.practicum.analyzer.model.Action;
import ru.practicum.analyzer.model.Condition;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class SnapshotProcessorJob {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    private final ScenarioRepository repository;
    private final ActionMapper actionMapper;

    public void start(SensorsSnapshotAvro snapshotAvro) {
        log.info("Получен Snapshot: {}", snapshotAvro);

        String hubId = snapshotAvro.getHubId();

        List<Scenario> scenarios = repository.findByHubId(hubId);

        if (scenarios.isEmpty()) {
            log.debug("Для hub c id: {} отсутствуют сценарии", hubId);
            return;
        }

        scenarios.stream()
                .filter(scenario -> isConditionEqualSnapshot(scenario.getConditions(), snapshotAvro))
                .forEach(this::processAction);
    }

    private boolean isConditionEqualSnapshot(Map<String, Condition> conditionMap, SensorsSnapshotAvro avro) {
        return conditionMap.entrySet()
                .stream()
                .allMatch(condition -> isConditionValue(condition.getKey(), condition.getValue(), avro));
    }

    private boolean isConditionValue(String sensorId, Condition condition, SensorsSnapshotAvro snapshot) {
        if (!snapshot.getSensorsState().containsKey(sensorId)) {
            return false;
        }

        SensorStateAvro avro = snapshot.getSensorsState().get(sensorId);
        ConditionTypeAvro conditionTypeAvro = condition.getType();
        ConditionOperationAvro conditionOperationAvro = condition.getOperation();
        Object date = avro.getData();

        return switch (date) {
            case ClimateSensorAvro cs -> {
                if (conditionTypeAvro.equals(ConditionTypeAvro.TEMPERATURE)) {
                    yield compare(cs.getTemperatureC(), condition.getValue(), conditionOperationAvro);
                }
                if (conditionTypeAvro.equals(ConditionTypeAvro.HUMIDITY)) {
                    yield compare(cs.getHumidity(), condition.getValue(), conditionOperationAvro);
                }
                if (conditionTypeAvro.equals(ConditionTypeAvro.CO2LEVEL)) {
                    yield compare(cs.getCo2Level(), condition.getValue(), conditionOperationAvro);
                }
                yield false;
            }
            case LightSensorAvro ls -> {
                if (conditionTypeAvro.equals(ConditionTypeAvro.LUMINOSITY)) {
                    yield compare(ls.getLuminosity(), condition.getValue(), conditionOperationAvro);
                }
                yield false;
            }
            case SwitchSensorAvro ss -> {
                if (conditionTypeAvro.equals(ConditionTypeAvro.SWITCH)) {
                    yield compare(ss.getState() ? 1 : 0, condition.getValue(), conditionOperationAvro);
                }
                yield false;
            }
            case MotionSensorAvro ms -> {
                if (conditionTypeAvro.equals(ConditionTypeAvro.MOTION)) {
                    yield compare(ms.getMotion() ? 1 : 0, condition.getValue(), conditionOperationAvro);
                }
                yield false;
            }
            case TemperatureSensorAvro ts -> {
                if (conditionTypeAvro.equals(ConditionTypeAvro.TEMPERATURE)) {
                    yield compare(ts.getTemperatureC(), condition.getValue(), conditionOperationAvro);
                }
                yield false;
            }
            default -> false;
        };
    }

    private boolean compare(int sensorValue, int expected, ConditionOperationAvro operation) {
        return switch (operation) {
            case GREATER_THAN -> sensorValue > expected;
            case LOWER_THAN -> sensorValue < expected;
            case EQUALS -> sensorValue == expected;
            default -> false;
        };
    }

    private void processAction(Scenario scenario) {
        log.info("Активировался сценарий для hub c id: {}", scenario.getHubId());
        for (Map.Entry<String, Action> actionEntry : scenario.getActions().entrySet()) {
            Action action = actionEntry.getValue();
            String sensorId = actionEntry.getKey();
            DeviceActionRequest deviceActionRequest = actionMapper.toDeviceActionRequest(scenario, action, sensorId);
            log.info("Начинаю отправку в hub_router - DeviceActionRequest {}", deviceActionRequest);
            try {
                hubRouterClient.handleDeviceAction(deviceActionRequest);
                log.debug("");
            } catch (Exception e) {
                log.error("Ошибка при отправке действия в hub_router DeviceActionRequest {}",
                        deviceActionRequest, e);
            }


        }
    }
}
