package ru.practicum.hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.hub.model.hubs.DeviceAddedEvent;
import ru.practicum.hub.model.hubs.DeviceRemovedEvent;
import ru.practicum.hub.model.hubs.ScenarioAddedEvent;
import ru.practicum.hub.model.hubs.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Mapper(componentModel = "spring")
public interface HubMapper {

    @Named("deviceAddedEventToAvro")
    @Mapping(source = "deviceType", target = "type")
    DeviceAddedEventAvro toDeviceAddedEventAvro(DeviceAddedEvent event);

    @Named("scenarioAddedEventToAvro")
    ScenarioAddedEventAvro toScenarioAddedEventAvro(ScenarioAddedEvent event);

    @Named("scenarioRemovedEventToAvro")
    ScenarioRemovedEventAvro toScenarioRemovedEventAvro(ScenarioRemovedEvent event);

    @Named("deviceRemovedEventToAvro")
    DeviceRemovedEventAvro toDeviceRemovedEventAvro(DeviceRemovedEvent event);

    @Mapping(source = "event", target = "payload", qualifiedByName = "deviceAddedEventToAvro")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "timestamp", target = "timestamp")
    HubEventAvro toHubEventAvroFromDeviceAddedEvent(DeviceAddedEvent event);

    @Mapping(source = "event", target = "payload", qualifiedByName = "scenarioAddedEventToAvro")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "timestamp", target = "timestamp")
    HubEventAvro toHubEventAvroFromScenarioAddedEvent(ScenarioAddedEvent event);

    @Mapping(source = "event", target = "payload", qualifiedByName = "scenarioRemovedEventToAvro")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "timestamp", target = "timestamp")
    HubEventAvro toHubEventAvroFromScenarioRemovedEvent(ScenarioRemovedEvent event);

    @Mapping(source = "event", target = "payload", qualifiedByName = "deviceRemovedEventToAvro")
    @Mapping(source = "hubId", target = "hubId")
    @Mapping(source = "timestamp", target = "timestamp")
    HubEventAvro toHubEventAvroFromDeviceRemovedEvent(DeviceRemovedEvent event);


}
