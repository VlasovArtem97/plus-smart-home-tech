package ru.practicum.contract.interactionapi.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckNegativeValueValidator.class)
public @interface CheckNegativeValueInMapProduct {

    String message() default "Количество товара не должно быть отрицательным";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
