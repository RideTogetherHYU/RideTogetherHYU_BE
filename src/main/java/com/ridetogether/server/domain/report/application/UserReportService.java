package com.ridetogether.server.domain.report.application;

import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.report.Model.HandleStatus;
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

import java.util.List;

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

    public Report saveReport(ReportSaveDto reportSaveDto) {
        if (reportSaveDto.getReportTitle().isEmpty()) {
            throw new ErrorHandler(ErrorStatus.REPORT_TITLE_NULL);
        }
        if (reportSaveDto.getReportContent().isEmpty()) {
            throw new ErrorHandler(ErrorStatus.REPORT_CONTENT_NULL);
        }
        // 여기도 나중에 매칭 넣기
        Report report = Report.builder()
                .reporter(reportSaveDto.getReporter())
//                .reported(reportSaveDto.getReported())
                .reportTitle(reportSaveDto.getReportTitle())
                .reportContent(reportSaveDto.getReportContent())
                .images(reportSaveDto.getImages())
                .build();


        report.setReportHandleStatus(HandleStatus.WAITING);
        reportRepository.save(report);
        return report;
    }

     // 특정 멤버가 작성한 거 가져오고 싶은데 .... @백도현오빠
    public List<Report> getMyReports(Member member) {

        List<Report> reportList = reportRepository.findAllByReporter(member);
        if (reportList.isEmpty()) {
            throw new ErrorHandler(ErrorStatus.REPORT_NOT_FOUND);
        }
        return reportList;
    }

    public ReportDetailInfoResponseDto getMyReportDetail(Long reportIdx) {
        Report report = reportRepository.findByIdx(reportIdx)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.REPORT_NOT_FOUND));

        return ReportDetailInfoResponseDto.builder()
//                .reported(report.getReported())
                .reportTitle(report.getReportTitle())
                .reportContent(report.getReportContent())
                .images(report.getImages())
                .build();

    }

    public ReportInfoResponseDto updateReport(Report updatedreport) {
        Report originReport = reportRepository.findByIdx(updatedreport.getIdx())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.REPORT_NOT_FOUND));

        originReport.updateReport(updatedreport);
        return ReportInfoResponseDto.builder()
                .idx(originReport.getIdx())
                .reportTitle(originReport.getReportTitle())
                .reportContent(originReport.getReportContent())
                .images(originReport.getImages())
                .build();
    }

    public ReportDeleteResponseDto deleteReport(Report report) {
        Report deleteReport = reportRepository.findByIdx(report.getIdx()).orElseThrow(() -> new ErrorHandler(ErrorStatus.REPORT_NOT_FOUND));
        reportRepository.deleteById(deleteReport.getIdx());

        return ReportDeleteResponseDto.builder()
                .isSuccess(true)
                .build();
    }

}
