package com.explosion204.wclookup.controller.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ErrorResponseUtil {
    private static final String ERROR_MESSAGE = "errorMessage";

    public Map<String, Object> buildErrorResponseMap(String errorMessage) {
        Map<String, Object> map = new HashMap<>();
        map.put(ERROR_MESSAGE, errorMessage);

        return map;
    }

    public ResponseEntity<Object> buildErrorResponseEntity(HttpStatus status, String errorMessage) {
        Map<String, Object> body = buildErrorResponseMap(errorMessage);

        return new ResponseEntity<>(body, status);
    }
}
