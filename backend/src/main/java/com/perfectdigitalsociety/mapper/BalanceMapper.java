package com.perfectdigitalsociety.mapper;

import com.perfectdigitalsociety.dto.response.BalanceEventResponse;
import com.perfectdigitalsociety.dto.response.SystemBalanceResponse;
import com.perfectdigitalsociety.dto.response.UserBalanceResponse;
import com.perfectdigitalsociety.entity.BalanceEvent;
import com.perfectdigitalsociety.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BalanceMapper {
    
    /**
     * Convert BalanceEvent entity to BalanceEventResponse DTO
     */
    @Mapping(target = "triggerType", expression = "java(balanceEvent.getTriggerType().toString())")
    @Mapping(target = "triggeredBy", source = "triggeredBy.id")
    @Mapping(target = "triggeredByUsername", source = "triggeredBy.username")
    BalanceEventResponse toBalanceEventResponse(BalanceEvent balanceEvent);
    
    /**
     * Convert list of BalanceEvent entities to list of BalanceEventResponse DTOs
     */
    List<BalanceEventResponse> toBalanceEventResponseList(List<BalanceEvent> balanceEvents);
    
    /**
     * Convert User entity to UserBalanceResponse DTO
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "freedomScore", source = "user.freedomScore")
    @Mapping(target = "securityScore", source = "user.securityScore")
    @Mapping(target = "reputationScore", source = "user.reputationScore")
    @Mapping(target = "balanceRatio", expression = "java(calculateBalanceRatio(user))")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    UserBalanceResponse toUserBalanceResponse(User user);
    
    /**
     * Convert list of User entities to list of UserBalanceResponse DTOs
     */
    List<UserBalanceResponse> toUserBalanceResponseList(List<User> users);
    
    /**
     * Create SystemBalanceResponse with provided values
     */
    @Mapping(target = "currentFreedomLevel", source = "freedomLevel")
    @Mapping(target = "currentSecurityLevel", source = "securityLevel")
    @Mapping(target = "balanceScore", source = "balanceScore")
    @Mapping(target = "lastUpdated", source = "lastUpdated")
    @Mapping(target = "trend", source = "trend")
    SystemBalanceResponse toSystemBalanceResponse(
        Integer freedomLevel, 
        Integer securityLevel, 
        Double balanceScore, 
        LocalDateTime lastUpdated, 
        String trend
    );
    
    /**
     * Convert BalanceEvent.TriggerType enum to string
     */
    default String mapTriggerType(BalanceEvent.TriggerType triggerType) {
        return triggerType != null ?  triggerType.toString() : null;
    }
    
    /**
     * Convert string to BalanceEvent.TriggerType enum
     */
    default BalanceEvent.TriggerType mapTriggerType(String triggerType) {
        return triggerType != null ? BalanceEvent.TriggerType.valueOf(triggerType.toUpperCase()) : null;
    }
    
    /**
     * Calculate balance ratio for user
     */
    default Double calculateBalanceRatio(User user) {
        if (user.getSecurityScore() == null || user.getSecurityScore() == 0) {
            return user.getFreedomScore() != null && user.getFreedomScore() > 0 ? 
                Double.MAX_VALUE : 1.0;
        }
        if (user.getFreedomScore() == null) {
            return 0.0;
        }
        return (double) user.getFreedomScore() / user.getSecurityScore();
    }
}