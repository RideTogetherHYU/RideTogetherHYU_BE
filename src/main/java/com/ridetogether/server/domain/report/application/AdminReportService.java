package com.ridetogether.server.domain.report.application;

import com.ridetogether.server.domain.report.Model.HandleStatus;
import com.ridetogether.server.domain.report.converter.ReportDtoConverter;
import com.ridetogether.server.domain.report.dao.ReportRepository;
import com.ridetogether.server.domain.report.domain.Report;
import com.ridetogether.server.domain.report.dto.ReportResponseDto;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.ridetogether.server.domain.report.dto.ReportResponseDto.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminReportService {

    /**
     * 처리중인 신고내역 조회
     * 처리 완료된 신고내역 조회
     * 인덱스로 신고내역 조회
     *
     * 신고 받은 거로 제재 만들기 -> soon
     *
     */
    private final ReportRepository reportRepository;

    public List<ReportDetailInfoResponseDto> getAllCompleteReport() {
        List<Report> allByHandleStatus = reportRepository.findAllByHandleStatus(HandleStatus.COMPLETE);
        List<ReportDetailInfoResponseDto> dto = new ArrayList<>();
        if (allByHandleStatus.isEmpty()) {
            throw new ErrorHandler(ErrorStatus.REPORT_NOT_FOUND);
        }
        for (Report report : allByHandleStatus) {
            dto.add(ReportDtoConverter.convertReportToDetailInfoDto(report));
        }
        return dto;
    }
    public List<ReportDetailInfoResponseDto> getAllWaitingReport() {
        List<Report> allByHandleStatus = reportRepository.findAllByHandleStatus(HandleStatus.WAITING);
        List<ReportDetailInfoResponseDto> lst = new ArrayList<>();
        if (allByHandleStatus.isEmpty()) {
            throw new ErrorHandler(ErrorStatus.REPORT_NOT_FOUND);
        }
        for (Report byHandleStatus : allByHandleStatus) {
            lst.add(ReportDtoConverter.convertReportToDetailInfoDto(byHandleStatus));
        }
        return lst;
    }
    public List<ReportDetailInfoResponseDto> getAllCompanionReport() {
        List<Report> allByHandleStatus = reportRepository.findAllByHandleStatus(HandleStatus.COMPANION);
        List<ReportDetailInfoResponseDto> lst = new ArrayList<>();
        if (allByHandleStatus.isEmpty()) {
            throw new ErrorHandler(ErrorStatus.REPORT_NOT_FOUND);
        }
        for (Report byHandleStatus : allByHandleStatus) {
            lst.add(ReportDtoConverter.convertReportToDetailInfoDto(byHandleStatus));
        }
        return lst;
    }

    public Report getReportByIdx(Long idx) {
        Report report = reportRepository.findByIdx(idx).orElseThrow(() -> new ErrorHandler(ErrorStatus.REPORT_NOT_FOUND));
        return report;
    }
}
