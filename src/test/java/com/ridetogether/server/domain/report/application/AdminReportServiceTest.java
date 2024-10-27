package com.ridetogether.server.domain.report.application;

import com.ridetogether.server.domain.member.MemberServiceTest;
import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.member.dto.MemberDto;
import com.ridetogether.server.domain.member.model.*;
import com.ridetogether.server.domain.report.dao.ReportRepository;
import com.ridetogether.server.domain.report.domain.Report;
import com.ridetogether.server.domain.report.dto.ReportDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.ridetogether.server.global.config.SecurityConfig.passwordEncoder;

@SpringBootTest
class AdminReportServiceTest {

    /*
     * 처리중인 신고내역 조회
     * 처리 완료된 신고내역 조회
     * 인덱스로 신고내역 조회
     */

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    AdminReportService adminReportService;

    @Autowired
    MemberService memberService;

    @BeforeEach
    void signup() {
        Member member = Member.builder()
                .memberId("qazplm12093")
                .password(passwordEncoder().encode("1234"))
                .name("hana")
                .email("asdf@hanyang.ac.kr")
                .nickName("waan")
                .gender(Gender.FEMALE)
                .kakaoPayUrl("aa")
                .account("aa")
                .accountBank(Bank.HANA_BANK)
                .role(Role.ROLE_ADMIN)
                .activeState(ActiveState.ACTIVE)
                .studentStatus(StudentStatus.STUDENT)
                .build();

        memberRepository.save(member);
    }


}