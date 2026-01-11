package ru.practicum.contract.interactionapi.dto.shoppingstore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ProductCategory {
    LIGHTING,
    CONTROL,
    SENSORS;

    public static ProductCategory toCategoryFromString(String category) {
        for (ProductCategory categ : values()) {
            if (categ.name().equalsIgnoreCase(category.trim())) {
                return categ;
            }
        }
        log.error("Неизвестная категория товара: {}", category);
        throw new IllegalStateException("Неизвестная категория товара: " + category);
    }
}
