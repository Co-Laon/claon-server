package com.claon.center.common;

import com.claon.center.common.exception.ErrorCode;
import com.claon.center.common.exception.FeignClientException;
import com.claon.center.common.exception.InternalServerErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Slf4j
public class GlobalFeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        ErrorResponse error = getBody(response);

        return new FeignClientException(response.status(), error.errorCode, error.message);
    }

    private ErrorResponse getBody(Response response) {
        try (InputStream bodyIs = response.body().asInputStream()) {
            return new ObjectMapper()
                    .findAndRegisterModules()
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .readValue(bodyIs, ErrorResponse.class);
        } catch (IOException e) {
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public record ErrorResponse(Integer errorCode, String message, LocalDateTime timeStamp) {
    }
}
