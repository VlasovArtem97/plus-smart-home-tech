package ru.practicum.hub.model.hubs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.hub.model.hubs.deviceenum.ActionEnum;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {

    @Size(min = 3, max = 2147483647)
    @NotBlank
    private String name;

    @NotNull
    @Valid
    private List<ScenarioCondition> conditions;

    @NotNull
    @Valid
    private List<DeviceAction> actions;

    @Override
    public ActionEnum getType() {
        return ActionEnum.SCENARIO_ADDED;
    }
}
