package com.ridetogether.server.domain.report.dto;

import com.ridetogether.server.domain.image.domain.Image;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.member.model.Role;
import com.ridetogether.server.domain.report.Model.HandleStatus;
import com.ridetogether.server.domain.report.Model.IsReporter;
import lombok.Builder;
import lombok.Data;

import java.util.List;

public class ReportResponseDto {

    @Data
    @Builder
    public static class ReportUpdateResponseDto { // 신고 업데이트 응답
        private Long idx;
        private String reportTitle;
        private String reportContent;
        private List<Image> images;
    }
    @Data
    @Builder
    public static class ReportDetailInfoResponseDto { // 신고 세부사항 응답
        private Long idx;
        private Member reporter;
        private String reportedId;
        private String reportTitle;
        private String reportContent;
        private List<Image> images;
    }

    @Data
    @Builder
    public static class ReportDeleteResponseDto { // 신고 삭제 응답
        private boolean isSuccess;
    }

    @Data
    @Builder
    public static class ReportSimpleGetResponseDto { // 내가 한 신고 목록 조회 응답
        private Long idx;
        private String reportedId;
        private String reportTitle;
        private HandleStatus handleStatus;
    }


}
