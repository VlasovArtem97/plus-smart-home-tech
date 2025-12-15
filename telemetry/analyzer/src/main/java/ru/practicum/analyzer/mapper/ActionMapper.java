package ru.practicum.analyzer.mapper;

import com.google.protobuf.Timestamp;
import org.mapstruct.*;
import ru.practicum.analyzer.model.Action;
import ru.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface ActionMapper {

    @Mapping(source = "scenario.name", target = "scenarioName")
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "action", expression = "java(toDeviceActionProto(action, sensorId))")
    DeviceActionRequest toDeviceActionRequest(Scenario scenario, Action action, String sensorId);

    @AfterMapping
    default void toTimestamp(@MappingTarget DeviceActionRequest.Builder deviceActionRequest) {
        Instant instant = Instant.now();
        deviceActionRequest.setTimestamp(Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build());
    }

    @Named("toDeviceActionProto")
    DeviceActionProto toDeviceActionProto(Action avro, String sensorId);
}
