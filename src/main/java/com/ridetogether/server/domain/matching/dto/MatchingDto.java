package com.ridetogether.server.domain.matching.dto;

import com.ridetogether.server.domain.matching.model.MatchingStatus;
import com.ridetogether.server.domain.member.model.Gender;
import com.ridetogether.server.domain.member.model.PayType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Builder
    @Data
    public static class UpdateMatchingDto {
        private String title;
        private String ridingTime;
        private int participantCount;
        private int maxParticipantCount;
        private String departure;
        private String destination;
        private MatchingStatus matchingStatus;
    }

}
