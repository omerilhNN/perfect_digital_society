package com.perfectdigitalsociety.controller;

import com.perfectdigitalsociety.dto.response.StatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/health")
@Slf4j
public class HealthController {

    /**
     * Basic health check endpoint
     */
    @GetMapping
    public ResponseEntity<StatusResponse> healthCheck() {
        log.debug("Health check requested");

        StatusResponse response = new StatusResponse();
        response.setSuccess(true);
        response.setMessage("Perfect Digital Society API is running successfully!");
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * API version information
     */
    @GetMapping("/version")
    public ResponseEntity<StatusResponse> getVersion() {
        log.debug("Version information requested");

        StatusResponse response = new StatusResponse();
        response.setSuccess(true);
        response.setMessage("Perfect Digital Society API v1.0.0 - Spring Boot 4.0.0");
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}
