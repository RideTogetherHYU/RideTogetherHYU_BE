package com.ridetogether.server.domain.report.application;

import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.report.Model.HandleStatus;
import com.ridetogether.server.domain.report.converter.ReportDtoConverter;
import com.ridetogether.server.domain.report.dao.ReportRepository;
import com.ridetogether.server.domain.report.domain.Report;
import com.ridetogether.server.domain.report.dto.ReportDto;
import com.ridetogether.server.domain.report.dto.ReportRequestDto;
import com.ridetogether.server.domain.report.dto.ReportResponseDto;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ridetogether.server.domain.report.converter.ReportDtoConverter.*;
import static com.ridetogether.server.domain.report.dto.ReportDto.*;
import static com.ridetogether.server.domain.report.dto.ReportRequestDto.*;
import static com.ridetogether.server.domain.report.dto.ReportResponseDto.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    /**
     * 신고 등록
     * 본인이 한 신고 내역 조회
     * 신고 내역 상세 조회
     * 신고 수정
     * 신고 삭제
     *
     */

    public ReportDetailInfoResponseDto saveReport(ReportSaveDto reportSaveDto) {
        if (reportSaveDto.getReportTitle().isEmpty()) {
            throw new ErrorHandler(ErrorStatus.REPORT_TITLE_NULL);
        }
        if (reportSaveDto.getReportContent().isEmpty()) {
            throw new ErrorHandler(ErrorStatus.REPORT_CONTENT_NULL);
        }
        if (!memberRepository.existsByMemberId(reportSaveDto.getReportedMemberId())) {
            throw new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }
        Member member = memberRepository.findByIdx(reportSaveDto.getReporter().getIdx()).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        if (member.isAdmin()) {
            throw new ErrorHandler(ErrorStatus._BAD_REQUEST);
        }
        // 여기도 나중에 매칭 넣기
        Report report = Report.builder()
                .reporter(reportSaveDto.getReporter())
                .reportedMemberId(reportSaveDto.getReportedMemberId())
                .reportTitle(reportSaveDto.getReportTitle())
                .reportContent(reportSaveDto.getReportContent())
                .images(reportSaveDto.getImages())
                .build();


        report.setReportHandleStatus(HandleStatus.WAITING);
        reportRepository.save(report);
        return convertReportToDetailInfoDto(report);
    }

    public ReportDetailInfoResponseDto getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new ErrorHandler(ErrorStatus.REPORT_NOT_FOUND));
        return ReportDetailInfoResponseDto.builder()
                .idx(report.getIdx())
                .reporter(report.getReporter())
                .reportedId(report.getReportedMemberId())
                .reportTitle(report.getReportTitle())
                .reportContent(report.getReportContent())
                .images(report.getImages())
                .build();
    }

    public List<ReportSimpleGetResponseDto> getMyReports(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        List<Report> reportList = member.getReports();
        List<ReportSimpleGetResponseDto> dtoList = new ArrayList<>();

        if (reportList.isEmpty()) {
            throw new ErrorHandler(ErrorStatus.REPORT_NOT_FOUND);
        }
        reportList.forEach(report -> dtoList.add(ReportDtoConverter.convertReportToSimpleGetDto(report)));
        return dtoList;
    }

    public ReportDetailInfoResponseDto getMyReportDetail(Long reportIdx) {
        Report report = reportRepository.findByIdx(reportIdx)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.REPORT_NOT_FOUND));

        return ReportDetailInfoResponseDto.builder()
                .reporter(report.getReporter())
                .reportedId(report.getReportedMemberId())
                .reportTitle(report.getReportTitle())
                .reportContent(report.getReportContent())
                .images(report.getImages())
                .build();

    }

    public ReportUpdateResponseDto updateReport(ReportDetailInfoResponseDto updatedreport) {
        Report originReport = reportRepository.findByIdx(updatedreport.getIdx())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.REPORT_NOT_FOUND));

        return ReportUpdateResponseDto.builder()
                .idx(updatedreport.getIdx())
                .reportTitle(updatedreport.getReportTitle())
                .reportContent(updatedreport.getReportContent())
                .images(updatedreport.getImages())
                .build();
    }

    public ReportDeleteResponseDto deleteReport(ReportDetailInfoResponseDto report) {
        Report deleteReport = reportRepository.findByIdx(report.getIdx()).orElseThrow(() -> new ErrorHandler(ErrorStatus.REPORT_NOT_FOUND));
        reportRepository.deleteById(deleteReport.getIdx());

        return ReportDeleteResponseDto.builder()
                .isSuccess(true)
                .build();
    }

}
