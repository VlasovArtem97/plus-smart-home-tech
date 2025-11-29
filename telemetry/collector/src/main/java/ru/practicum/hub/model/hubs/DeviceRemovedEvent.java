package ru.practicum.hub.model.hubs;

import jakarta.validation.constraints.NotBlank;
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
public class DeviceRemovedEvent extends HubEvent {

    @NotBlank
    private String id;

    @Override
    public ActionEnum getType() {
        return ActionEnum.DEVICE_REMOVED;
    }
}
