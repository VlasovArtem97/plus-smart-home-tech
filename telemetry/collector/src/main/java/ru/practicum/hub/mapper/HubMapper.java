package ru.practicum.hub.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface HubMapper {


    @Mapping(target = "payload", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    HubEventAvro toHubEventAvroFromHubEventProto(HubEventProto event);

    @AfterMapping
    default void getHubEventProto(@MappingTarget HubEventAvro.Builder builder, HubEventProto event) {
        switch (event.getPayloadCase()) {
            case DEVICE_ADDED -> builder.setPayload(mapDeviceAddedProtoToAvro(event.getDeviceAdded()));
            case DEVICE_REMOVED -> builder.setPayload(mapDeviceRemovedProtoToAvro(event.getDeviceRemoved()));
            case SCENARIO_ADDED -> builder.setPayload(mapScenarioAddedEventProtoToAvro(event.getScenarioAdded()));
            case SCENARIO_REMOVED -> builder.setPayload(mapScenarioRemovedEventToProto(event.getScenarioRemoved()));
            default -> {
                throw new IllegalStateException("Неизвестный тип: " + event.getPayloadCase());
            }
        }
        builder.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()));
    }

    /*
    DEVICE_ADDED
     */
    @ValueMapping(target = MappingConstants.THROW_EXCEPTION, source = "UNRECOGNIZED")
    DeviceAddedEventAvro mapDeviceAddedProtoToAvro(DeviceAddedEventProto proto);

    /*
    DEVICE_REMOVED
     */
    DeviceRemovedEventAvro mapDeviceRemovedProtoToAvro(DeviceRemovedEventProto proto);

    /*
    SCENARIO_ADDED
     */
    @AfterMapping
    default void mapConditionValue(@MappingTarget List<ScenarioConditionAvro> avroList, List<ScenarioConditionProto> protoList) {
        Map<String, ScenarioConditionAvro> avroMap = avroList.stream()
                .collect(Collectors.toMap(ScenarioConditionAvro::getSensorId, Function.identity()));
        protoList
                .forEach(proto -> {
                    ScenarioConditionAvro avro = avroMap.get(proto.getSensorId());
                    if (avro != null) {
                        avro.setValue(mapConditionValueProtoToAvro(proto));
                    }
                });
    }

    default Object mapConditionValueProtoToAvro(ScenarioConditionProto proto) {
        return switch (proto.getValueCase()) {
            case BOOL_VALUE -> proto.getBoolValue();
            case INT_VALUE -> proto.getIntValue();
            default -> null;
        };
    }

    @Named("mapScenarioCondition")
    @ValueMapping(target = MappingConstants.THROW_EXCEPTION, source = "UNRECOGNIZED")
    List<ScenarioConditionAvro> mapConditions(List<ScenarioConditionProto> conditionList);

    @Mapping(source = "conditionList", target = "conditions", qualifiedByName = "mapScenarioCondition")
    @Mapping(source = "actionList", target = "actions")
    @ValueMapping(target = MappingConstants.THROW_EXCEPTION, source = "UNRECOGNIZED")
    ScenarioAddedEventAvro mapScenarioAddedEventProtoToAvro(ScenarioAddedEventProto proto);


    /*
    SCENARIO_REMOVED
     */
    ScenarioRemovedEventAvro mapScenarioRemovedEventToProto(ScenarioRemovedEventProto proto);

}

