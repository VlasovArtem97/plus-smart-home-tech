package ru.practicum.hub.service;

import ru.practicum.hub.model.hubs.HubEvent;
import ru.practicum.hub.model.sensors.SensorEvent;

public interface CollectorService {

    void addSensors(SensorEvent sensorEvent);

    void addHubs(HubEvent hubEvent);
}
