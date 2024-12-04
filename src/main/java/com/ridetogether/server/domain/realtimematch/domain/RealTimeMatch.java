package com.ridetogether.server.domain.realtimematch.domain;

import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

// Redis에 저장될 데이터 구조 -> 빌더형태로
// Redis 사용방법 (repo, template 조사 후 고민)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RealTimeMatch {

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

    private int nowParticipantCnt;

    private int maxParticipantCnt; // Matching에서 가져와서 저장 해두기

    private LocalDate expiredAt;

}
