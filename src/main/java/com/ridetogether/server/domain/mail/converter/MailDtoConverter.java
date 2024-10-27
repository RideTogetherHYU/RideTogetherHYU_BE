package com.ridetogether.server.domain.mail.converter;

import com.ridetogether.server.domain.mail.dto.MailResponseDto.CheckMailResponseDto;
import com.ridetogether.server.domain.mail.dto.MailResponseDto.SendMailResponseDto;

public class MailDtoConverter {

    public static SendMailResponseDto convertAuthCodeToDto(int authNumber) {
        return SendMailResponseDto.builder()
                .authNumber(Integer.toString(authNumber))
                .build();
    }

    public static CheckMailResponseDto convertCheckMailResultToDto(boolean result) {
        return CheckMailResponseDto.builder()
                .isSuccess(result)
                .build();
    }
}
