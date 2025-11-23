package ru.practicum.hub.model.hubs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.hub.model.hubs.deviceenum.ActionEnum;
import ru.practicum.hub.model.hubs.deviceenum.DeviceType;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {

    @NotBlank
    private String id;

    @NotNull
    private DeviceType deviceType;

    @Override
    public ActionEnum getType() {
        return ActionEnum.DEVICE_ADDED;
    }
}
