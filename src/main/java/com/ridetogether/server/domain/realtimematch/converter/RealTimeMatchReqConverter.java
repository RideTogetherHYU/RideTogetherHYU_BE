package com.ridetogether.server.domain.realtimematch.converter;

import java.time.LocalDateTime;
import static com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchRequestDto.*;

public class RealTimeMatchReqConverter {

    public static CreateRealTimeMatchRequestDto convertCreateMatchDto(Long matchingId, int maxParticipantCnt, LocalDateTime expired) {
        return CreateRealTimeMatchRequestDto.builder()
                .matchingId(matchingId)
                .maxParticipantCnt(maxParticipantCnt)
                .expiredAt(expired)
                .build();

    }

    public static DeleteRealTimeMatchRequest convertDeleteMatchDto(Long realTimeMatchId, Long participantId) {
        return DeleteRealTimeMatchRequest.builder()
                .realTimeMatchId(realTimeMatchId)
                .participantId(participantId)
                .build();
    }

    public static RealTimeMatchInfoRequest convertGetInfoMatchDto(Long realTimeMatchId) {
        return RealTimeMatchInfoRequest.builder()
                .realTimeMatchId(realTimeMatchId)
                .build();

    }

    public static EnterAndLeaveMatchRequest convertEnterAndLeaveMatchDto(Long realTimeMatchId, Long participantId) {
        return EnterAndLeaveMatchRequest.builder()
                .realTimeMatchId(realTimeMatchId)
                .participantId(participantId)
                .build();
    }
}
