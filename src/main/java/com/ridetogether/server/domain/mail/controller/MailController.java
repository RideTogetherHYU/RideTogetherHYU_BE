package com.ridetogether.server.domain.mail.controller;


import com.ridetogether.server.domain.mail.dto.MailDto.CheckMailRequestDto;
import com.ridetogether.server.domain.mail.dto.MailResponseDto;
import com.ridetogether.server.domain.mail.dto.MailResponseDto.CheckMailResponseDto;
import com.ridetogether.server.domain.mail.application.MailService;
import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
//@RequestMapping("/api/email")
@RestController
public class MailController {

    private final MailService mailService;
    private final MemberService memberService;


    @PostMapping("/api/email/send")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<MailResponseDto.SendMailResponseDto> sendHanyangMail(@RequestParam(value = "email") String email) {
        return ApiResponse.onSuccess(mailService.sendEmail(email));
    }

    @PostMapping("/api/email/confirm")
    public ApiResponse<CheckMailResponseDto> confirmEmail(@RequestBody @Valid CheckMailRequestDto dto) {
        return ApiResponse.onSuccess(mailService.checkEmail(dto.getEmail(), dto.getAuthNumber()));
    }

    @PostMapping("/api/member/email/send")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<MailResponseDto.SendMailResponseDto> sendVerificationHanyangMail(@RequestParam(value = "email") String email) {
        return ApiResponse.onSuccess(mailService.sendEmail(email));
    }

    @PostMapping("/api/member/email/confirm")
    public ApiResponse<CheckMailResponseDto> confirmVerificationHanyangEmail(@RequestBody @Valid CheckMailRequestDto dto) {
        CheckMailResponseDto checkMailResponseDto = mailService.checkEmail(dto.getEmail(), dto.getAuthNumber());
        if (checkMailResponseDto.isSuccess()) {
            Member loginMember = SecurityUtil.getLoginMember()
                    .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
            memberService.updateStudentStatusToStudent(loginMember.getIdx());
        }
        return ApiResponse.onSuccess(checkMailResponseDto);
    }

}
