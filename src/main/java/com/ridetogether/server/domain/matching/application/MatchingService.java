package com.ridetogether.server.domain.matching.application;

import com.ridetogether.server.domain.chatroom.dao.ChatRoomRepository;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.matching.dao.MatchingRepository;
import com.ridetogether.server.domain.matching.dao.MemberMatchingRepository;
import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.domain.matching.domain.MemberMatching;
import com.ridetogether.server.domain.matching.dto.MatchingDto.CreateMatchingDto;
import com.ridetogether.server.domain.matching.dto.MatchingResponseDto;
import com.ridetogether.server.domain.matching.dto.MatchingResponseDto.CreateMatchingResponseDto;
import com.ridetogether.server.domain.matching.dto.MatchingResponseDto.JoinMatchingResponseDto;
import com.ridetogether.server.domain.matching.dto.MatchingResponseDto.MatchingInfoResponseDto;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.member.model.PayType;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;
    private final MemberMatchingRepository memberMatchingRepository;
    private final ChatRoomRepository chatRoomRepository;

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
        member.addMemberMatching(memberMatching);
        matching.addMemberMatching(memberMatching);
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

//        member.getMemberMatching().add(memberMatching);
//        matching.getMemberMatching().add(memberMatching);

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

    public MatchingInfoResponseDto getMatchingInfo(Long matchingIdx) {
        Matching matching = matchingRepository.findByIdx(matchingIdx)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MATCHING_NOT_FOUND));
        Long chatRoomIdx = chatRoomRepository.findByChatRoomId(matchingIdx)
                .map(ChatRoom::getChatRoomId)
                .orElse(null);
        return MatchingInfoResponseDto.builder()
                .matchingIdx(matching.getIdx())
                .hostMemberIdx(matching.getHostMemberIdx())
                .title(matching.getTitle())
                .ridingTime(matching.getRidingTime())
                .participantCount(matching.getParticipantCount() + "")
                .departure(matching.getDeparture())
                .destination(matching.getDestination())
                .matchingStatus(matching.getMatchingStatus().name())
                .matchingGender(matching.getMatchingGender().name())
                .payTypes(matching.getPayTypes().stream()
                        .map(PayType::toName)
                        .toList()
                )
                .chatRoomIdx(chatRoomIdx)
                .expiredAt(matching.getExpiredAt().toString())
                .price(matching.getPrice())
                .build();
    }

    public String cancelJoinMatching(Long matchingIdx, Long memberIdx) {
//        Matching matching = matchingRepository.findByIdx(matchingIdx)
//                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MATCHING_NOT_FOUND));
//        Member member = memberRepository.findByIdx(memberIdx)
//                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
//        MemberMatching memberMatching = memberMatchingRepository.findByMemberAndMatching(member, matching)
//                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_MATCHING_NOT_FOUND));
//        memberMatchingRepository.delete(memberMatching);
//        matching.minusParticipantCount();
        return "success";
    }

    // 반환타입 이렇게 해도 되나
    public String enterTaxiPrice(Long matchingIdx, int price) {
        Matching matching = matchingRepository.findByIdx(matchingIdx)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MATCHING_NOT_FOUND));

        if (price < 0) throw new ErrorHandler(ErrorStatus.PRICE_INVALIDATE);
        matching.updatePrice(price);
        return "success";
    }
}
