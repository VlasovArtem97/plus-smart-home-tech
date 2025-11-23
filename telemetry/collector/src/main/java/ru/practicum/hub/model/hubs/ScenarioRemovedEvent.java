package ru.practicum.hub.model.hubs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.hub.model.hubs.deviceenum.ActionEnum;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {

    @Size(min = 3, max = 2147483647)
    @NotBlank
    private String name;

    @Override
    public ActionEnum getType() {
        return ActionEnum.SCENARIO_REMOVED;
    }
}
