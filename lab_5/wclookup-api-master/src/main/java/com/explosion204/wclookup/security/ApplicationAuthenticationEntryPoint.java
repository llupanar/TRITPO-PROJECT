package com.explosion204.wclookup.security;

import com.explosion204.wclookup.controller.util.ErrorResponseUtil;
import com.explosion204.wclookup.service.MessageSourceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class ApplicationAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final String UNAUTHORIZED_MESSAGE = "unauthorized";
    private static final String CONTENT_TYPE_JSON = "application/json";

    private final MessageSourceService messageSourceService;
    private final ErrorResponseUtil errorResponseUtil;

    public ApplicationAuthenticationEntryPoint(
            MessageSourceService messageSourceService,
            ErrorResponseUtil errorResponseUtil
    ) {
        this.messageSourceService = messageSourceService;
        this.errorResponseUtil = errorResponseUtil;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        String errorMessage = messageSourceService.getString(UNAUTHORIZED_MESSAGE);
        Map<String, Object> errorResponse = errorResponseUtil.buildErrorResponseMap(errorMessage);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(CONTENT_TYPE_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter()
                .write(objectMapper.writeValueAsString(errorResponse));
    }
}
