package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("ru.practicum.contract.interactionapi.feignclient")
public class OrderApp {

    public static void main(String[] arg) {
        SpringApplication.run(OrderApp.class, arg);
    }
}
