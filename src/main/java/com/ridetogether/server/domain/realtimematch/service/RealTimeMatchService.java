package com.ridetogether.server.domain.realtimematch.service;

import com.ridetogether.server.domain.matching.application.MatchingService;
import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.domain.matching.model.MatchingStatus;
import com.ridetogether.server.domain.realtimematch.domain.RealTimeMatch;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;

import static com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchRequestDto.*;
import static com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchResponseDto.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealTimeMatchService {

    private static final String ID_COUNTER = "match:id:counter";
    private final static String MATCH_PREFIX = "realtimeMatch:";

    private final RedisTemplate<String, RealTimeMatch> redisTemplate;
    private final MatchingService matchingService;


    /*
    필요기능
    1. 매칭 생성
    2. 매칭 삭제
    3. 매칭 조회
    4. 인원 추가
    5. 인원 제거
    * */

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

        addParticipant(match, requestDto.getParticipantId());
        redisTemplate.opsForValue().set(MATCH_PREFIX + requestDto.getRealTimeMatchId(), match);
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
        if (matching.getHostMemberIdx().equals(requestDto.getParticipantId())) {
            // 매칭을 삭제하시겠습니까 ? 경고문 띄울 수 있게 향후 작업
            redisTemplate.delete(MATCH_PREFIX + requestDto.getRealTimeMatchId());
        }
        if (!match.getRestParticipantsId().contains(requestDto.getParticipantId())) {
            throw new ErrorHandler(ErrorStatus.MATCHING_NOT_PARTICIPANT);
        }

        removeParticipant(match, requestDto.getParticipantId());
        redisTemplate.opsForValue().set(MATCH_PREFIX + requestDto.getRealTimeMatchId(), match);
    }


    private RealTimeMatch findRealTimeMatchById(Long realTimeMatchId) {
        return redisTemplate.opsForValue().get(MATCH_PREFIX + realTimeMatchId);
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
