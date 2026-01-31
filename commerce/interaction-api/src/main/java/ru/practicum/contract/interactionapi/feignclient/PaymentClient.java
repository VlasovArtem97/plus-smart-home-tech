package ru.practicum.contract.interactionapi.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import ru.practicum.contract.interactionapi.config.Config;
import ru.practicum.contract.interactionapi.contract.payment.PaymentOperation;
import ru.practicum.contract.interactionapi.feignclient.fallbackfactory.PaymentFallbackFactory;

@FeignClient(name = "payment", path = "/api/v1/payment", configuration = Config.class,
        fallbackFactory = PaymentFallbackFactory.class)
@Validated
public interface PaymentClient extends PaymentOperation {
}
