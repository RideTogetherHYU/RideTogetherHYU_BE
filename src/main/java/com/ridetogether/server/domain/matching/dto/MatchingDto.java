package com.ridetogether.server.domain.matching.dto;

import com.ridetogether.server.domain.member.model.Gender;
import com.ridetogether.server.domain.member.model.PayType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

public class MatchingDto {

    @Builder
    @Data
    public static class CreateMatchingDto{
        private Long hostMemberIdx;
        private String title;
        private String ridingTime;
        private String departure;
        private String destination;
        private Integer maxParticipantCnt;
        private Gender matchingGender;
        private List<PayType> payTypes;
        private LocalDate expiredAt;
    }

}
