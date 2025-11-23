package ru.practicum.hub.model.hubs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.hub.model.hubs.deviceenum.ConditionOperation;
import ru.practicum.hub.model.hubs.deviceenum.ConditionType;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioCondition {

    //    @NotBlank
    private String sensorId;

    //    @NotNull
    private ConditionType type;

    //    @NotNull
    private ConditionOperation operation;

    private Integer value;
}
