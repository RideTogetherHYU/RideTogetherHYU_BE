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

import static com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchDto.*;

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
    public void createMatch(Long realTimeMatchId, Long matchingId, int maxParticipantCnt, LocalDateTime expiredAt) {

        RealTimeMatch matchDto = RealTimeMatch.builder()
                .idx(realTimeMatchId)
                .matchingIdx(matchingId)
                .restParticipantsId(new HashSet<>())
                .nowParticipantCnt(1)
                .maxParticipantCnt(maxParticipantCnt)
                .matchingStatus(MatchingStatus.WAITING)
                .expiredAt(expiredAt)
                .build()
        ;

        String key = MATCH_PREFIX + realTimeMatchId;
        redisTemplate.opsForValue().set(key, matchDto, Duration.between(LocalDateTime.now(), expiredAt));
    }

    // 매칭삭제
    public void deleteMatch(Long realTimeMatchId, Long participantId) {
        RealTimeMatch match = findRealTimeMatchById(realTimeMatchId);
        Matching matching = matchingService.findByIdx(match.getMatchingIdx());

        // 방장만 삭제 가능
        if (!matching.getHostMemberIdx().equals(participantId)) {
            throw new ErrorHandler(ErrorStatus.MATCHING_NOT_HOST);
        }

        redisTemplate.delete(MATCH_PREFIX + realTimeMatchId);
    }

    // 매칭 정보 가져오기
    public RealTimeMatchInfoDto getRealTimeMatchInfo(Long realTimeMatchId) {
        RealTimeMatch match = findRealTimeMatchById(realTimeMatchId);
        Long hostMemberIdx = matchingService.findByIdx(match.getMatchingIdx()).getHostMemberIdx();

        return RealTimeMatchInfoDto.builder()
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
    public void enterMatching(Long realTimeMatchId, Long participantId) {
        RealTimeMatch match = findRealTimeMatchById(realTimeMatchId);

        if (match.getNowParticipantCnt() >= match.getMaxParticipantCnt()) {
            throw new ErrorHandler(ErrorStatus.MATCHING_PARTICIPANT_FULL);
        }

        if (match.getRestParticipantsId().contains(participantId)) {
            throw new ErrorHandler(ErrorStatus.MATCHING_ALREADY_PARTICIPANT);
        }

        addParticipant(match, participantId);
        redisTemplate.opsForValue().set(MATCH_PREFIX + realTimeMatchId, match);
    }

    // 매칭 나가기 (방장 제외)
    public void leaveMatching(Long realTimeMatchId, Long participantId) {
        RealTimeMatch match = findRealTimeMatchById(realTimeMatchId);
        Matching matching = matchingService.findByIdx(match.getMatchingIdx());

        if (matching.getHostMemberIdx().equals(participantId)) {
            // 매칭을 삭제하시겠습니까 ? 경고문 띄울 수 있게 향후 작업
            redisTemplate.delete(MATCH_PREFIX + realTimeMatchId);
        }
        if (!match.getRestParticipantsId().contains(participantId)) {
            throw new ErrorHandler(ErrorStatus.MATCHING_NOT_PARTICIPANT);
        }

        removeParticipant(match, participantId);
        redisTemplate.opsForValue().set(MATCH_PREFIX + realTimeMatchId, match);
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
