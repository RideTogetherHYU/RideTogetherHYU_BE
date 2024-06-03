package com.ridetogether.server.domain.report.dao;

import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.report.Model.HandleStatus;
import com.ridetogether.server.domain.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findByIdx(Long idx); // use posting idx

    List<Report> findAllByReporter(Member member);
    List<Report> findAllByHandleStatus(HandleStatus handleStatus);

//    boolean existsByReport(Long idx);

}
