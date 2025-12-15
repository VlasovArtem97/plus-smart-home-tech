package ru.practicum.analyzer.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SnapshotAvroDeserializer extends BaseAvroDeserializer<SensorsSnapshotAvro> {

    public SnapshotAvroDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}
