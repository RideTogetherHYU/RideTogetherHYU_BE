package com.ridetogether.server.domain.report.controller;

import com.ridetogether.server.domain.report.Model.HandleStatus;
import com.ridetogether.server.domain.report.application.AdminReportService;
import com.ridetogether.server.domain.report.application.UserReportService;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ridetogether.server.domain.report.converter.ReportDtoConverter.*;
import static com.ridetogether.server.domain.report.dto.ReportDto.*;
import static com.ridetogether.server.domain.report.dto.ReportRequestDto.*;
import static com.ridetogether.server.domain.report.dto.ReportResponseDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final AdminReportService adminReportService;
    private final UserReportService userReportService;

    /**
     * -유저-
     * 유저 신고
     * 자신이 한 신고 조회 (전체조회)
     * 자신이 한 신고 조회 (세부내용)
     * 신고 수정
     * 신고 삭제
     * -어드민-
     * 신고를 받은 사람한테 제재 주기 -> 제재에 구현
     * 대기중인 신고 조회
     * 처리완료된 신고 조회
     *
     */

    // user
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReportDetailInfoResponseDto> reportUser(@Valid @RequestBody ReportUpdateRequestDto report) throws Exception {
        ReportSaveDto reportSaveDto = convertRequestToSaveDto(report);
        return ApiResponse.onSuccess(userReportService.saveReport(reportSaveDto));
    }

    // 이렇게 ㄴ하는거 맞나
    @GetMapping("/member_id={member_id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ReportSimpleGetResponseDto>> getMyReports(@PathVariable("member_id") String memberId) {

        return ApiResponse.onSuccess(userReportService.getMyReports(memberId));
    }

    @GetMapping("?report_id={report_id}")
    public ApiResponse<ReportDetailInfoResponseDto> getMyReportDetailInfo(@PathVariable("report_id") Long reportId) {
         return ApiResponse.onSuccess(userReportService.getMyReportDetail(reportId));
    }

    @PatchMapping("?report_id={report_id}")
    public ApiResponse<ReportUpdateResponseDto> updateMyReport(@PathVariable("report_id") Long reportId) {
        ReportDetailInfoResponseDto reportById = userReportService.getReportById(reportId);
        return ApiResponse.onSuccess(userReportService.updateReport(reportById));
    }

    @DeleteMapping("?report_id={report_id}")
    public ApiResponse<ReportDeleteResponseDto> deleteMyReport(@PathVariable("report_id") Long reportId) {
        ReportDetailInfoResponseDto reportById = userReportService.getReportById(reportId);
        return ApiResponse.onSuccess(userReportService.deleteReport(reportById));
    }

    // admin
    @GetMapping("?status={status}")
    public ApiResponse<List<ReportDetailInfoResponseDto>> getReportByStatus(@PathVariable("status") HandleStatus handleStatus) {
        if (handleStatus.equals(HandleStatus.WAITING)) {
            return ApiResponse.onSuccess(adminReportService.getAllWaitingReport());
        }
        else if (handleStatus.equals(HandleStatus.COMPLETE)) {
            return ApiResponse.onSuccess(adminReportService.getAllCompleteReport());
        }
        return ApiResponse.onSuccess(adminReportService.getAllCompanionReport());
    }









}
