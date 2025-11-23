package ru.practicum.hub.model.sensors;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Min(-50)
    @Max(50)
    private int temperatureC;

    @Min(-58)
    @Max(122)
    private int temperatureF;

    @Override
    public SensorEventType getType() {
        return null;
    }
}
