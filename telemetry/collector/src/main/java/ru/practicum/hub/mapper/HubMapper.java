package ru.practicum.hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.hub.model.hubs.DeviceAddedEvent;
import ru.practicum.hub.model.hubs.DeviceRemovedEvent;
import ru.practicum.hub.model.hubs.ScenarioAddedEvent;
import ru.practicum.hub.model.hubs.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Mapper(componentModel = "spring")
public interface HubMapper {

//    @Mapping(source = "deviceType", target = "type")
//    DeviceAddedEventAvro toDeviceAddedEventAvro(DeviceAddedEvent event);
//
//    DeviceRemovedEventAvro toDeviceRemovedEventAvro(DeviceRemovedEvent event);
//
//    ScenarioAddedEventAvro toScenarioAddedEventAvro(ScenarioAddedEvent event);
//
//    ScenarioRemovedEventAvro toScenarioRemovedEventAvro(ScenarioRemovedEvent event);

    @Mapping(source = "event", target = "payload")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "timestamp", target = "timestamp")
    HubEventAvro toHubEventAvroFromDeviceAddedEvent(DeviceAddedEvent event);

    @Mapping(source = "event", target = "payload")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "timestamp", target = "timestamp")
    HubEventAvro toHubEventAvroFromScenarioAddedEvent(ScenarioAddedEvent event);

    @Mapping(source = "event", target = "payload")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "timestamp", target = "timestamp")
    HubEventAvro toHubEventAvroFromScenarioRemovedEvent(ScenarioRemovedEvent event);

    @Mapping(source = "event", target = "payload")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "timestamp", target = "timestamp")
    HubEventAvro toHubEventAvroFromDeviceRemovedEvent(DeviceRemovedEvent event);


}
