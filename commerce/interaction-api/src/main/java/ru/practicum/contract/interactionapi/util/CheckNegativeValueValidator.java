package ru.practicum.contract.interactionapi.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class CheckNegativeValueValidator implements ConstraintValidator<CheckNegativeValueInMapProduct, Map<UUID, Long>> {

    @Override
    public boolean isValid(Map<UUID, Long> uuidLongMap, ConstraintValidatorContext constraintValidatorContext) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<UUID, Long> product : uuidLongMap.entrySet()) {
            UUID id = product.getKey();
            Long quantity = product.getValue();
            boolean isValue = quantity > 0;
            if (!isValue) {
                log.error("Для продукта с id: [ {} ] передано отрицательное количество: [ {} ]", id, quantity);
                stringBuilder.append("Для продукта с id: [").append(id).append(" ] передано отрицательное число: [ ")
                        .append(quantity).append(" ]").append("\n");
            }
        }
        if (!stringBuilder.isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(stringBuilder.toString())
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
