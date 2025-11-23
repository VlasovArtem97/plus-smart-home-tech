package ru.practicum.hub.model.sensors;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.hub.model.sensors.senserenum.SensorEventType;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = SensorEventType.LIGHT_SENSOR_EVENT_NAME),
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = SensorEventType.CLIMATE_SENSOR_EVENT_NAME),
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = SensorEventType.MOTION_SENSOR_EVENT_NAME),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = SensorEventType.TEMPERATURE_SENSOR_EVENT_NAME),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = SensorEventType.SWITCH_SENSOR_EVENT_NAME)
})
@ToString
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public abstract class SensorEvent {

    @NotBlank
    private String id;
    @NotBlank
    private String hubId;
    @Builder.Default
    private Instant timestamp = Instant.now();


    @NotNull
    public abstract SensorEventType getType();
}
