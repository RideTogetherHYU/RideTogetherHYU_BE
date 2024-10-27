package com.ridetogether.server.domain.report.dto;

import com.ridetogether.server.domain.image.domain.Image;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.report.domain.ReportStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

public class ReportRequestDto {

    @Data
    @Builder
    public static class ReportUpdateRequestDto {
        private Member reporter;
        private String reportedMemberId;
        private ReportStatus reportStatus;
        private String reportTitle;
        private String reportContent;
        private List<Image> images;
    }

    @Data
    @Builder
    public static class ReportGetRequestDto {
        private String reporter;
        private String reported;
    }
}
