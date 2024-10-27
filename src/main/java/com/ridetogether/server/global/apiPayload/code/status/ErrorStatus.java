package com.ridetogether.server.global.apiPayload.code.status;

import com.ridetogether.server.global.apiPayload.code.BaseErrorCode;
import com.ridetogether.server.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "접근 권한이 없는 요청입니다."),
    _LOGIN_FAILURE(HttpStatus.NOT_FOUND, "COMMON404", "요청 리소스를 찾을 수 없습니다."),
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _SERVICE_UNAVAILABLE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "COMMON503", "서버가 일시적으로 사용중지 되었습니다."),

    //유저 응답
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "유저가 존재하지 않습니다."),
    MEMBER_EMAIL_PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "MEMBER4002", "이메일 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4003", "중복된 이메일입니다."),
    MEMBER_NICKNAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4004", "중복된 닉네임입니다."),
    MEMBER_STATE_INACTIVE(HttpStatus.BAD_REQUEST, "MEMBER4005", "휴면상태의 유저입니다."),
    MEMBER_STATE_NOT_STUDENT(HttpStatus.BAD_REQUEST, "MEMBER4006", "한양대학교 학생 인증이 미완료된 사용자입니다."),
    MEMBER_STATE_NOT_ADMIN(HttpStatus.FORBIDDEN, "MEMBER4007", "접근 권한이 없습니다"),
    MEMBER_LOGIN_NOT_SUPPORT(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "MEMBER4015", "지원되지 않는 로그인 형식입니다."),

    //이메일 응답
    EMAIL_SEND_FAIL(HttpStatus.BAD_REQUEST, "EMAIL4001", "이메일 전송에 실패하였습니다."),
    EMAIL_NOT_HANYANG_EMAIL(HttpStatus.BAD_REQUEST, "EMAIL4002", "한양대학교 이메일이 아닙니다."),

    //사진 응답
    IMAGE_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, "IMG4001", "사진 업로드에 실패하였습니다."),
    IMAGE_DOWNLOAD_FAIL(HttpStatus.NOT_FOUND, "IMG4002", "사진 다운로드에 실패하였습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMG4003", "사진 조회에 실패하였습니다."),

    // 신고 응답
    REPORT_NOT_FOUND(HttpStatus.BAD_REQUEST, "REPORT4001", "신고내역이 존재하지 않습니다."),
    REPORT_TITLE_NULL(HttpStatus.BAD_REQUEST, "REPORT4002", "신고제목을 작성해주세요."),
    REPORT_CONTENT_NULL(HttpStatus.BAD_REQUEST, "REPORT4003", "신고내용을 작성해주세요."),

    //푸시알림 응답
    ALARM_SEND_FAIL(HttpStatus.BAD_REQUEST,"ALARM4001", "알림 보내기를 실패하였습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
            .message(message)
            .code(code)
            .isSuccess(false)
            .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
            .message(message)
            .code(code)
            .isSuccess(false)
            .httpStatus(httpStatus)
            .build();
    }
}