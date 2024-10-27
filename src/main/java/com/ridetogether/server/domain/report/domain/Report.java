package com.ridetogether.server.domain.report.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ridetogether.server.domain.image.domain.Image;
import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.report.Model.HandleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * 신고 당한 사람의 아이디만 넣어두기
 * 신고가 들어왔을 때 근거가 충분하다고 생각이 들면, 신고 당한 사람에게 경고
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Report {

    @Id
    @Column(name = "report_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @OneToMany(mappedBy = "report")
    @JsonIgnore
    private List<ReportStatus> reportStatus;

    private String reporterMemberId;

    private String reportedMemberId;

    private String reportMatchingId;

    private String reportTitle;

    private String reportContent;

    @OneToMany(mappedBy = "report")
    @JsonIgnore
    private List<Image> images;

    // How to timestamp

    @Enumerated(EnumType.STRING)
    private HandleStatus handleStatus;

    public void setReportHandleStatus(HandleStatus handleStatus) {
        this.handleStatus = handleStatus;
    }

    public void setReportTitle(String title) {
        this.reportTitle = title;
    }
    public void setReportContent(String content) {
        this.reportContent = content;
    }

    public void setImages(List<Image> imges) {
        this.images = imges;
    }

    public void updateReport(Report report) {
        this.setReportTitle(report.getReportTitle());
        this.setReportContent(report.getReportContent());
        this.setImages(report.getImages());
    }

    public boolean isComplete() {
        return this.handleStatus == HandleStatus.COMPLETE;
    }

    public boolean isWaiting() {
        return this.handleStatus == HandleStatus.WAITING;
    }

    public boolean isCompanion() {
        return this.handleStatus == HandleStatus.COMPANION;
    }

}
