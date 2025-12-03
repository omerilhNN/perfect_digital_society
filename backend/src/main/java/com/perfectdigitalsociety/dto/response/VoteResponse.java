package com.perfectdigitalsociety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponse {
    
    private Long ruleId;
    private Integer totalVotes;
    private Integer positiveVotes;
    private Integer negativeVotes;
    private Boolean userVote;
}