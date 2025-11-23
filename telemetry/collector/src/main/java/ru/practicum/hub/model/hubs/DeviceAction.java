package ru.practicum.hub.model.hubs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.hub.model.hubs.deviceenum.ScenarioActions;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceAction {

    //    @NotBlank
    private String sensorId;

    //    @NotNull
    private ScenarioActions type;

    private Integer value;
}
