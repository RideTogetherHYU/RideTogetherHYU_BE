package com.ridetogether.server.domain.realtimematch.dto;

import com.ridetogether.server.domain.matching.model.MatchingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

public class RealTimeMatchDto {

    @Data
    @Builder
    public static class RealTimeMatchInfoDto {
        private Long realTimeMatchId;
        private Long hostId;
        private Set<Long> restMemberIds;
        int restParticipantsCnt;
        int maxParticipantsCnt;
        MatchingStatus matchingStatus;
        LocalDateTime expired;
    }
}
