package ru.practicum.contract.interactionapi.feignclient.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import ru.practicum.contract.interactionapi.exception.fiegnclient.InternalServerErrorException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.NotAuthorizedException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class FeignDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        try (InputStream is = response.body().asInputStream()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            if (response.status() == 400 || response.status() == 404) {
                return new NotFoundException(body);
            }
            if (response.status() == 401) {
                return new NotAuthorizedException("Resource not found for method: " + s + "\n" + body);
            }
            if (response.status() == 500) {
                return new InternalServerErrorException("Server error occurred. \n" + body);
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка в декодере: " + e.getMessage());
        }
        return defaultDecoder.decode(s, response);
    }
}
