package com.perfectdigitalsociety.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfectdigitalsociety.dto.response.StatusResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private final ObjectMapper objectMapper;


    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException ex) throws IOException {
        
        log.error("Responding with unauthorized error. Message: {}", ex.getMessage());
        
        StatusResponse errorResponse = new StatusResponse();
        errorResponse.setSuccess(false);
        errorResponse.setMessage("Unauthorized access - Please provide valid authentication token");
        errorResponse.setTimestamp(LocalDateTime.now());
        
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        objectMapper.writeValue(httpServletResponse.getWriter(), errorResponse);
    }
}