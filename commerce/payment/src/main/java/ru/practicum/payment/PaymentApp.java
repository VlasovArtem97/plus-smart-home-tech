package ru.practicum.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("ru.practicum.contract.interactionapi.feignclient")
public class PaymentApp {

    public static void main(String[] arg) {
        SpringApplication.run(PaymentApp.class, arg);
    }
}
