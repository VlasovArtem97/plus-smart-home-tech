package ru.practicum.contract.interactionapi.feignclient.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ru.practicum.contract.interactionapi.exception.fiegnclient.BadRequestException;
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
            if (response.status() == 400) {
                return new BadRequestException(body, HttpStatus.BAD_REQUEST);
            }
            if (response.status() == 404) {
                return new NotFoundException(body, HttpStatus.NOT_FOUND);
            }
            if (response.status() == 401) {
                return new NotAuthorizedException(body, HttpStatus.UNAUTHORIZED);
            }
            if (response.status() == 500) {
                return new InternalServerErrorException("Server error occurred. \n" + body,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка в декодере: " + e.getMessage());
        }
        return defaultDecoder.decode(s, response);
    }
}
