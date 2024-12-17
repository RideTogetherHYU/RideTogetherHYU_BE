package com.ridetogether.server.domain.realtimematch.service;

import com.ridetogether.server.domain.matching.application.MatchingService;
import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.domain.matching.dto.MatchingResponseDto;
import com.ridetogether.server.domain.matching.model.MatchingStatus;
import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.realtimematch.domain.RealTimeMatch;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static com.ridetogether.server.domain.matching.dto.MatchingResponseDto.*;
import static com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchRequestDto.*;
import static com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchResponseDto.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealTimeMatchService {

    private static final String ID_COUNTER = "match:id:counter";
    private final static String MATCH_PREFIX = "realtimeMatch:";

    private final RedisTemplate<String, Object> redisTemplate;
    private Map<String, ChannelTopic> topics;

    private final MatchingService matchingService;

    private final MemberRepository memberRepository;


    /*
    필요기능
    1. 매칭 생성
    2. 매칭 삭제
    3. 매칭 조회
    4. 인원 추가
    5. 인원 제거
    * */

    @PostConstruct
    private void init() {
        topics = new HashMap<>();
    }

    // PK 발급
    public Long generateRealTimeMatchId() {
        return redisTemplate.opsForValue().increment(ID_COUNTER);
    }

    // 매칭 생성
    public void createMatch(CreateRealTimeMatchRequestDto requestDto) {
        Long realTimeMatchId = generateRealTimeMatchId();
        RealTimeMatch matchDto = RealTimeMatch.builder()
                .idx(realTimeMatchId)
                .matchingIdx(requestDto.getMatchingId())
                .restParticipantsId(new HashSet<>())
                .nowParticipantCnt(1)
                .maxParticipantCnt(requestDto.getMaxParticipantCnt())
                .matchingStatus(MatchingStatus.WAITING)
                .expiredAt(requestDto.getExpiredAt())
                .build()
        ;

        String key = MATCH_PREFIX + realTimeMatchId;
        redisTemplate.opsForValue().set(key, matchDto, Duration.between(LocalDateTime.now(), requestDto.getExpiredAt()));
    }

    // 매칭삭제
    public void deleteMatch(DeleteRealTimeMatchRequest requestDto) {
        RealTimeMatch match = findRealTimeMatchById(requestDto.getRealTimeMatchId());
        Matching matching = matchingService.findByIdx(match.getMatchingIdx());

        // 방장만 삭제 가능
        if (!matching.getHostMemberIdx().equals(requestDto.getParticipantId())) {
            throw new ErrorHandler(ErrorStatus.MATCHING_NOT_HOST);
        }

        redisTemplate.delete(MATCH_PREFIX + requestDto.getRealTimeMatchId());
    }

    // 매칭 정보 가져오기
    public RealTimeMatchInfoResponseDto getRealTimeMatchInfo(RealTimeMatchInfoRequest requestDto) {
        RealTimeMatch match = findRealTimeMatchById(requestDto.getRealTimeMatchId());
        Long hostMemberIdx = matchingService.findByIdx(match.getMatchingIdx()).getHostMemberIdx();

        return RealTimeMatchInfoResponseDto.builder()
                .realTimeMatchId(match.getIdx())
                .hostId(hostMemberIdx)
                .restMemberIds(match.getRestParticipantsId())
                .restParticipantsCnt(match.getNowParticipantCnt())
                .maxParticipantsCnt(match.getMaxParticipantCnt())
                .matchingStatus(match.getMatchingStatus())
                .expired(match.getExpiredAt())
                .build()
        ;
    }

    // 매칭 참여
    public void enterMatching(EnterAndLeaveMatchRequest requestDto) {
        RealTimeMatch match = findRealTimeMatchById(requestDto.getRealTimeMatchId());
        Matching matching = matchingService.findByIdx(match.getMatchingIdx());
        Member member = memberRepository.findByIdx(requestDto.getParticipantId())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if (!member.getGender().equals(matching.getMatchingGender())) {
            throw new ErrorHandler(ErrorStatus.MATCHING_CANNOT_PARTICIPATE);
        }

        if (match.getMatchingStatus().equals(MatchingStatus.PROGRESS)) {
            throw new ErrorHandler(ErrorStatus.MATCHING_ALREADY_START);
        }

        if (match.getMatchingStatus().equals(MatchingStatus.FINISH)) {
            throw new ErrorHandler(ErrorStatus.MATCHING_ALREADY_FINISH);
        }

        if (match.getNowParticipantCnt() >= match.getMaxParticipantCnt()) {
            throw new ErrorHandler(ErrorStatus.MATCHING_PARTICIPANT_FULL);
        }

        if (match.getRestParticipantsId().contains(requestDto.getParticipantId())) {
            throw new ErrorHandler(ErrorStatus.MATCHING_ALREADY_PARTICIPANT);
        }

        // Topic 가져오기
        ChannelTopic topic = topics.computeIfAbsent(match.getIdx() + "", id -> {
            log.info("등록된 topic이 없습니다. 새로운 topic을 생성합니다. realTimeMatchId : {}", id);
            return new ChannelTopic(id);
        });

        // 참여자 추가
        addParticipant(match, requestDto.getParticipantId());

        // Redis에 덮어쓰기 (업데이트)
        String key = MATCH_PREFIX + requestDto.getRealTimeMatchId();
        redisTemplate.opsForValue().set(key, match);
    }

    // 매칭 나가기 (방장 제외)
    public void leaveMatching(EnterAndLeaveMatchRequest requestDto) {
        RealTimeMatch match = findRealTimeMatchById(requestDto.getRealTimeMatchId());
        Matching matching = matchingService.findByIdx(match.getMatchingIdx());

        if (match.getMatchingStatus().equals(MatchingStatus.PROGRESS)) {
            throw new ErrorHandler(ErrorStatus.MATCHING_ALREADY_START);
        }
        if (match.getMatchingStatus().equals(MatchingStatus.FINISH)) {
            throw new ErrorHandler(ErrorStatus.MATCHING_ALREADY_FINISH);
        }

        // 방장이 나가면 매칭 삭제
        if (matching.getHostMemberIdx().equals(requestDto.getParticipantId())) {
            redisTemplate.delete(MATCH_PREFIX + requestDto.getRealTimeMatchId());
            return;
        }

        if (!match.getRestParticipantsId().contains(requestDto.getParticipantId())) {
            throw new ErrorHandler(ErrorStatus.MATCHING_NOT_PARTICIPANT);
        }

        // 참여자 제거
        removeParticipant(match, requestDto.getParticipantId());

        // Redis에 덮어쓰기 (업데이트)
        String key = MATCH_PREFIX + requestDto.getRealTimeMatchId();
        redisTemplate.opsForValue().set(key, match);
    }


    public Set<Long> getParticipantsId(RealTimeMatchInfoRequest request) {
        RealTimeMatchInfoResponseDto info = getRealTimeMatchInfo(request);
        return info.getRestMemberIds();
    }

    private RealTimeMatch findRealTimeMatchById(Long realTimeMatchId) {
        String key = MATCH_PREFIX + realTimeMatchId;
        return (RealTimeMatch) redisTemplate.opsForValue().get(key);
    }

    private void addParticipant(RealTimeMatch match, Long participantId) {
        match.getRestParticipantsId().add(participantId);
        match.plusParticipantCount();
    }

    private void removeParticipant(RealTimeMatch match, Long participantId) {
        match.getRestParticipantsId().remove(participantId);
        match.minusParticipantCount();
    }

}
