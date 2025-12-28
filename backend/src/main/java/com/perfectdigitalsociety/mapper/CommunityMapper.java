package com.perfectdigitalsociety.mapper;

import com.perfectdigitalsociety.dto.response.CommunityMetricsResponse;
import com.perfectdigitalsociety.dto.response.CommunityRuleResponse;
import com.perfectdigitalsociety.dto.response.VoteResponse;
import com.perfectdigitalsociety.entity.CommunityRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CommunityMapper {
    
    /**
     * Convert CommunityRule entity to CommunityRuleResponse DTO
     */
    @Mapping(target = "ruleType", expression = "java(communityRule.getRuleType().toString())")
    @Mapping(target = "action", expression = "java(communityRule.getAction().toString())")
    @Mapping(target = "createdBy", source = "createdBy.id")
    @Mapping(target = "createdByUsername", source = "createdBy.username")
    CommunityRuleResponse toCommunityRuleResponse(CommunityRule communityRule);
    
    /**
     * Convert list of CommunityRule entities to list of CommunityRuleResponse DTOs
     */
    List<CommunityRuleResponse> toCommunityRuleResponseList(List<CommunityRule> communityRules);
    
    /**
     * Create VoteResponse with provided values
     */
    @Mapping(target = "ruleId", source = "ruleId")
    @Mapping(target = "totalVotes", source = "totalVotes")
    @Mapping(target = "positiveVotes", source = "positiveVotes")
    @Mapping(target = "negativeVotes", source = "negativeVotes")
    @Mapping(target = "userVote", source = "userVote")
    VoteResponse toVoteResponse(
        Long ruleId,
        Integer totalVotes,
        Integer positiveVotes,
        Integer negativeVotes,
        Boolean userVote
    );
    
    /**
     * Create CommunityMetricsResponse with provided values
     */
    @Mapping(target = "totalUsers", source = "totalUsers")
    @Mapping(target = "activeUsers", source = "activeUsers")
    @Mapping(target = "totalMessages", source = "totalMessages")
    @Mapping(target = "flaggedMessages", source = "flaggedMessages")
    @Mapping(target = "averageFreedomScore", source = "averageFreedomScore")
    @Mapping(target = "averageSecurityScore", source = "averageSecurityScore")
    @Mapping(target = "communityHealth", source = "communityHealth")
    @Mapping(target = "lastCalculated", source = "lastCalculated")
    CommunityMetricsResponse toCommunityMetricsResponse(
        Integer totalUsers,
        Integer activeUsers,
        Integer totalMessages,
        Integer flaggedMessages,
        Double averageFreedomScore,
        Double averageSecurityScore,
        Double communityHealth,
        LocalDateTime lastCalculated
    );
    
    /**
     * Convert CommunityRule.RuleType enum to string
     */
    default String mapRuleType(CommunityRule.RuleType ruleType) {
        return ruleType != null ? ruleType.toString() : null;
    }
    
    /**
     * Convert string to CommunityRule.RuleType enum
     */
    default CommunityRule.RuleType mapRuleType(String ruleType) {
        return ruleType != null ? CommunityRule.RuleType.valueOf(ruleType.toUpperCase()) : null;
    }
    
    /**
     * Convert CommunityRule.Action enum to string
     */
    default String mapAction(CommunityRule.Action action) {
        return action != null ? action.toString() : null;
    }
    
    /**
     * Convert string to CommunityRule.Action enum
     */
    default CommunityRule.Action mapAction(String action) {
        return action != null ?  CommunityRule.Action.valueOf(action.toUpperCase()) : null;
    }
}