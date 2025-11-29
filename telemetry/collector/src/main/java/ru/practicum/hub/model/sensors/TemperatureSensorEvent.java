package ru.practicum.hub.model.sensors;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.hub.model.sensors.senserenum.SensorEventType;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TemperatureSensorEvent extends SensorEvent {

    @Size(min = -50, max = 50)
    private int temperatureC;

    @Size(min = -58, max = 122)
    private int temperatureF;

    @Override
    public SensorEventType getType() {
        return null;
    }
}
