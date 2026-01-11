package ru.practicum.contract.interactionapi.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.contract.interactionapi.feignclient.decoder.FeignDecoder;

@Configuration
public class Config {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignDecoder();
    }
}
