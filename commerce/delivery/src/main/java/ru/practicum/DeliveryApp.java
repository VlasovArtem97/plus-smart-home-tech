package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("ru.practicum.contract.interactionapi.feignclient")
public class DeliveryApp {

    public static void main(String[] arg) {
        SpringApplication.run(DeliveryApp.class, arg);
    }
}
