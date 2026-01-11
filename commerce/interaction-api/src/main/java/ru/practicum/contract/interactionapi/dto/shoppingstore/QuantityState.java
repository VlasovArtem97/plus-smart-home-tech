package ru.practicum.contract.interactionapi.dto.shoppingstore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum QuantityState {
    ENDED,
    FEW,
    ENOUGH,
    MANY;

    public static QuantityState toQuantityStateFromString(String state) {
        for (QuantityState quantityState : values()) {
            if (quantityState.name().equalsIgnoreCase(state.trim())) {
                return quantityState;
            }
        }
        log.debug("Неизвестный статус товара: {}", state);
        throw new IllegalStateException("Неизвестный статус товара: " + state);
    }
}
