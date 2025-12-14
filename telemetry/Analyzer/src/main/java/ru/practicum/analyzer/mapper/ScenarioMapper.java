package ru.practicum.analyzer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.practicum.analyzer.model.Action;
import ru.practicum.analyzer.model.Condition;
import ru.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ScenarioMapper {

    @Mapping(source = "avro.conditions", target = "conditions", qualifiedByName = "toCondition")
    @Mapping(source = "avro.actions", target = "actions", qualifiedByName = "toAction")
    @Mapping(target = "id", ignore = true)
    Scenario toScenario(ScenarioAddedEventAvro avro, String hubId);

    @Mapping(source = "conditions", target = "conditions", qualifiedByName = "toCondition")
    @Mapping(source = "actions", target = "actions", qualifiedByName = "toAction")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hubId", ignore = true)
    void updateScenario(@MappingTarget Scenario scenario, ScenarioAddedEventAvro avro);

    @Named("toCondition")
    default Map<String, Condition> toCondition(List<ScenarioConditionAvro> avro) {
        return avro.stream()
                .collect(Collectors.toMap(
                        ScenarioConditionAvro::getSensorId,
                        con -> Condition.builder()
                                .type(con.getType())
                                .operation(con.getOperation())
                                .value(toValue(con.getValue()))
                                .build()
                ));
    }

    @Named("toAction")
    default Map<String, Action> toAction(List<DeviceActionAvro> avro) {
        return avro.stream()
                .collect(Collectors.toMap(
                        DeviceActionAvro::getSensorId,
                        act -> Action.builder()
                                .type(act.getType())
                                .value(act.getValue())
                                .build()
                ));
    }

    default Integer toValue(Object o) {
        return switch (o) {
            case Integer i -> i;
            case Boolean b -> b ? 1 : 0;
            default -> null;
        };
    }
}
