package ru.practicum.hub.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.hub.model.hubs.HubEvent;
import ru.practicum.hub.model.sensors.SensorEvent;
import ru.practicum.hub.service.CollectorService;

@RestController
@RequestMapping(path = "/events")
@Validated
@RequiredArgsConstructor
@Slf4j
public class CollectorController {

    private final CollectorService collectorService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/sensors")
    public void addSensorsEvent(@RequestBody @Valid SensorEvent sensorEvent) {
        log.info("Получен запрос на добавление событий датчиков: {}", sensorEvent);
        collectorService.addSensors(sensorEvent);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hubs")
    public void addHubEvent(@RequestBody @Valid HubEvent hubEvent) {
        log.info("Получен запрос на добавление событий хабов: {}", hubEvent);
        collectorService.addHubs(hubEvent);
    }
}
