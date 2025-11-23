package ru.practicum.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.practicum.avroserialization")
public class CollectionApp {
    public static void main(String[] args) {
        SpringApplication.run(CollectionApp.class, args);
    }
}
