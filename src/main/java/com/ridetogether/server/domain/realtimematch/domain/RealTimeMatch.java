package com.ridetogether.server.domain.realtimematch.domain;

import com.ridetogether.server.domain.matching.model.MatchingStatus;
import com.ridetogether.server.domain.realtimematch.model.RealTimeMatchStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// Redis에 저장될 데이터 구조 -> 빌더형태로
// Redis 사용방법 (repo, template 조사 후 고민)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class RealTimeMatch { // 매칭 대기상태일 때 멤버가 비확정적일 때 사용 -> DB연동 X

    /*
    * 매칭에 신청, 취소한 사람 관리
    * 현재 인원, 현재 상태 관리
    *
    * BaseRedisTemplateEntity 상속에 관련한 고민을 좀 더 해볼 것
    *
    * # 들어갈 속성
    * 매칭 상태
    * 매칭방 idx
    * 가능 인원
    * 현재 인원 -> 변동성이 크므로
    *
    * */

    private Long idx;

    private Long matchingIdx;

    private Set<Long> restParticipantsId = new HashSet<>();

    private int nowParticipantCnt;

    private int maxParticipantCnt; // Matching에서 가져와서 저장 해두기

    private RealTimeMatchStatus realTimeMatchStatus;

    private LocalDateTime expiredAt;

    public void plusParticipantCount() {
        this.nowParticipantCnt++;
    }

    public void minusParticipantCount() {
        this.nowParticipantCnt--;
    }

    public void updateStatusToReady() {
        this.realTimeMatchStatus = RealTimeMatchStatus.READY;
    }

    public void updateStatusToWait() {
        this.realTimeMatchStatus = RealTimeMatchStatus.WAIT;
    }

}
