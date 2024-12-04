package com.ridetogether.server.domain.matching.dto;

import lombok.Builder;
import lombok.Data;


import java.util.List;

public class MatchingRequestDto {

    @Builder
    @Data
    public static class CreateMatchingRequestDto{
        private String title;
        private String ridingTime;
        private String departure;
        private String destination;
        private String matchingGender;
        private Integer maxParticipantCnt;
        private List<String> payTypes;
        private String expiredAt;
    }

    @Builder
    @Data
    public static class UpdatePriceRequestDto {
        private int price;
    }
}
