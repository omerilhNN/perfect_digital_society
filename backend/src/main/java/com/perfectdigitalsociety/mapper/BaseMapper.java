package com.perfectdigitalsociety.mapper;

import com.perfectdigitalsociety.dto.response.StatusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface BaseMapper {
    
    /**
     * Create a standard StatusResponse
     */
    @Mapping(target = "success", source = "success")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    StatusResponse toStatusResponse(Boolean success, String message);
    
    /**
     * Create a successful StatusResponse
     */
    default StatusResponse toSuccessResponse(String message) {
        return toStatusResponse(true, message);
    }
    
    /**
     * Create a failed StatusResponse
     */
    default StatusResponse toFailureResponse(String message) {
        return toStatusResponse(false, message);
    }
    
    /**
     * Create StatusResponse with timestamp
     */
    @Mapping(target = "success", source = "success")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "timestamp", source = "timestamp")
    StatusResponse toStatusResponse(Boolean success, String message, LocalDateTime timestamp);
}