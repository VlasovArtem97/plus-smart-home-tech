package ru.practicum.hub.model.hubs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.hub.model.hubs.deviceenum.ActionEnum;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = ActionEnum.DEVICE_ADDED_NAME),
        @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = ActionEnum.DEVICE_REMOVED_NAME),
        @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = ActionEnum.SCENARIO_ADDED_NAME),
        @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = ActionEnum.SCENARIO_REMOVED_NAME),
})
@ToString
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public abstract class HubEvent {

    @NotBlank
    private String hubId;
    @Builder.Default
    private Instant timestamp = Instant.now();

    @NotNull
    public abstract ActionEnum getType();

}
