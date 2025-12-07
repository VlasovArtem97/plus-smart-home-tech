package ru.practicum.hub.service;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface CollectorService {

    void addSensors(SensorEventProto sensorEvent);

    void addHubs(HubEventProto hubEvent);
}
