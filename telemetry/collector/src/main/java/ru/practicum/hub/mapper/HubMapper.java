package ru.practicum.hub.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface HubMapper {

    @Mapping(source = "hubId", target = "hubId")
    @Mapping(target = "payload", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    HubEventAvro toHubEventAvroFromHubEventProto(HubEventProto event);

    @AfterMapping
    default void getHubEventProto(@MappingTarget HubEventAvro.Builder builder, HubEventProto event) {
        switch (event.getPayloadCase()) {
            case DEVICE_ADDED -> builder.setPayload(event.getDeviceAdded());
            case DEVICE_REMOVED -> builder.setPayload(event.getDeviceRemoved());
            case SCENARIO_ADDED -> builder.setPayload(event.getScenarioAdded());
            case SCENARIO_REMOVED -> builder.setPayload(event.getScenarioRemoved());
            default -> {
                throw new IllegalStateException("Неизвестный тип: " + event.getPayloadCase());
            }
        }
        builder.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()));
    }
}
