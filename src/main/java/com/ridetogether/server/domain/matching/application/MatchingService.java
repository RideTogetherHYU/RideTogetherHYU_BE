package com.ridetogether.server.domain.matching.application;

import com.ridetogether.server.domain.matching.dao.MatchingRepository;
import com.ridetogether.server.domain.matching.dao.MemberMatchingRepository;
import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.domain.matching.domain.MemberMatching;
import com.ridetogether.server.domain.matching.dto.MatchingDto.CreateMatchingDto;
import com.ridetogether.server.domain.matching.dto.MatchingResponseDto;
import com.ridetogether.server.domain.matching.dto.MatchingResponseDto.CreateMatchingResponseDto;
import com.ridetogether.server.domain.matching.dto.MatchingResponseDto.JoinMatchingResponseDto;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;
    private final MemberMatchingRepository memberMatchingRepository;

    private static final int MAX_PARTICIPANT_COUNT = 4;

    public CreateMatchingResponseDto createMatching(CreateMatchingDto dto) {
        Member member = memberRepository.findByIdx(dto.getHostMemberIdx())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Matching matching = Matching.builder()
                .hostMemberIdx(member.getIdx())
                .hostMemberNickName(member.getNickName())
                .title(dto.getTitle())
                .ridingTime(dto.getRidingTime())
                .participantCount(1)
                .departure(dto.getDeparture())
                .destination(dto.getDestination())
                .matchingGender(dto.getMatchingGender())
                .payTypes(dto.getPayTypes())
                .build();
        matchingRepository.save(matching);
        MemberMatching memberMatching = MemberMatching.builder()
                .member(member)
                .matching(matching)
                .build();

        memberMatchingRepository.save(memberMatching);
        member.getMemberMatching().add(memberMatching);
        matching.getMemberMatching().add(memberMatching);

        return CreateMatchingResponseDto.builder()
                .matchingIdx(matching.getIdx())
                .memberIdx(member.getIdx())
                .memberMatchingIdx(memberMatching.getIdx())
                .memberNickName(member.getNickName())
                .title(matching.getTitle())
                .isSuccess(true)
                .build();
    }

    public JoinMatchingResponseDto joinMatching(Long matchingIdx, Long memberIdx) {
        Matching matching = matchingRepository.findByIdx(matchingIdx)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MATCHING_NOT_FOUND));
        Member member = memberRepository.findByIdx(memberIdx)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        if (matching.getParticipantCount() == MAX_PARTICIPANT_COUNT) {
            throw new ErrorHandler(ErrorStatus.MATCHING_PARTICIPANT_FULL);
        }

        MemberMatching memberMatching = MemberMatching.builder()
                .member(member)
                .matching(matching)
                .build();
        memberMatchingRepository.save(memberMatching);

        member.getMemberMatching().add(memberMatching);
        matching.getMemberMatching().add(memberMatching);

        matching.plusParticipantCount();

        return JoinMatchingResponseDto.builder()
                .matchingIdx(matching.getIdx())
                .memberIdx(member.getIdx())
                .memberNickName(member.getNickName())
                .hostMemberIdx(matching.getHostMemberIdx())
                .hostMemberNickName(matching.getHostMemberNickName())
                .memberMatchingIdx(memberMatching.getIdx())
                .title(matching.getTitle())
                .isSuccess(true)
                .build();
    }


}
