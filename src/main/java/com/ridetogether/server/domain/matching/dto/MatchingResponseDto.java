package com.ridetogether.server.domain.matching.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public class MatchingResponseDto {

    @Builder
    @Data
    public static class CreateMatchingResponseDto {
        private Long matchingIdx;
        private String title;
        private Long memberIdx;
        private String memberNickName;
        private Long memberMatchingIdx;
        private Boolean isSuccess;
    }

    @Builder
    @Data
    public static class JoinMatchingResponseDto {
        private Long matchingIdx;
        private String title;
        private Long memberIdx;
        private String memberNickName;
        private Long hostMemberIdx;
        private String hostMemberNickName;
        private Long memberMatchingIdx;
        private Boolean isSuccess;
    }

    @Builder
    @Data
    public static class MatchingInfoResponseDto {
        private Long matchingIdx;
        private Long hostMemberIdx;
        private String title;
        private String ridingTime;
        private String participantCount;
        private String departure;
        private String destination;
        private String matchingStatus;
        private String matchingGender;
        private List<String> payTypes;
        private Long chatRoomIdx;
        private String expiredAt;
        private int price;
    }
}
