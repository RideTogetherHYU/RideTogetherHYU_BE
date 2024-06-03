package com.ridetogether.server.domain.mail.dto;

import lombok.Builder;
import lombok.Data;

public class MailResponseDto {

    @Builder
    @Data
    public static class SendMailResponseDto {
        private String authNumber;
    }

    @Data
    @Builder
    public static class CheckMailResponseDto {
        private boolean isSuccess;
    }
}
