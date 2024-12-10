package com.ridetogether.server.domain.realtimematch.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;


public class RealTimeMatchRequestDto {

    @Data
    @Builder
    public static class CreateRealTimeMatchRequestDto {
        private Long matchingId;
        private int maxParticipantCnt;
        private LocalDateTime expiredAt;
    }

    @Data
    @Builder
    public static class DeleteRealTimeMatchRequest {
        private Long realTimeMatchId;
        private Long participantId;
    }

    @Data
    @Builder
    public static class RealTimeMatchInfoRequest {
        private Long realTimeMatchId;
    }

    @Data
    @Builder
    public static class EnterAndLeaveMatchRequest {
        private Long realTimeMatchId;
        private Long participantId;
    }
}
