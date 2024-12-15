package com.ridetogether.server.domain.realtimematch.controller;

import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchRequestDto;
import com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchResponseDto;
import com.ridetogether.server.domain.realtimematch.service.RealTimeMatchService;
import com.ridetogether.server.domain.realtimematch.service.RedisMatchPub;
import com.ridetogether.server.domain.realtimematch.service.RedisMatchSub;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchRequestDto.*;
import static com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchResponseDto.*;

@RequiredArgsConstructor
@Controller
@Slf4j
public class RealTimeMatchController {

    // 매칭은 다대일 구조라서 pub/sub 구조를 사용하는 게 적합해보임

    private final SimpMessageSendingOperations messagingTemplate;
    private final RealTimeMatchService realTimeMatchService;
    private final MemberService memberService;

    private final RedisMatchPub redisMatchPub;
    private final RedisMatchSub redisMatchSub;

    @MessageMapping("/match/join")
    public void joinMatch(@Payload EnterAndLeaveMatchRequest request) {
        String memberId = SecurityUtil.getLoginMemberId().orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Member loginMember = memberService.findByMemberId(memberId);

        redisMatchPub.publish("match-join", request);
        // redis 매칭 데이터 업데이트
        realTimeMatchService.enterMatching(request);

        // 업데이트 상태 구독자들에게 브로드캐스트
        Long realTimeMatchId = request.getRealTimeMatchId();
        RealTimeMatchInfoResponseDto realTimeMatchInfo = realTimeMatchService.getRealTimeMatchInfo(new RealTimeMatchInfoRequest(realTimeMatchId));
        log.info("{} 님이 매칭에 참여하였습니다. 실시간 매칭아이디 = {} ", loginMember.getNickName(), realTimeMatchId);

        messagingTemplate.convertAndSend("/topic/realtime-match/" + realTimeMatchId, realTimeMatchInfo);
    }

    @MessageMapping("/match/{matchId}/leave")
    public void leaveMatch(@DestinationVariable Long realTimeMatchId,
                           @Payload EnterAndLeaveMatchRequest request) {
        String memberId = SecurityUtil.getLoginMemberId().orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Member loginMember = memberService.findByMemberId(memberId);

        redisMatchPub.publish("match-leave", request);

        realTimeMatchService.leaveMatching(request);

        // 업데이트 상태 구독자들에게 브로드캐스트
        RealTimeMatchInfoResponseDto realTimeMatchInfo = realTimeMatchService.getRealTimeMatchInfo(new RealTimeMatchInfoRequest(realTimeMatchId));
        log.info("{} 님이 매칭에서 나갔습니다. 실시간 매칭아이디 = {} ", loginMember.getNickName(), realTimeMatchId);

        // 경로를 같게해야할지 다르게 해야할지 ...-> 같게하자
        messagingTemplate.convertAndSend("/topic/realtime-match/" + realTimeMatchId, realTimeMatchInfo);
    }

    @MessageMapping("/match/{matchId}/info")
    public void getRealTimeMatchInfo(@DestinationVariable Long realTimeMatchId) {
        RealTimeMatchInfoResponseDto info = realTimeMatchService.getRealTimeMatchInfo(new RealTimeMatchInfoRequest(realTimeMatchId));
        messagingTemplate.convertAndSend("/topic/realtime-match/" + realTimeMatchId, info);
    }

    @MessageMapping("/match/{matchId}/participants")
    public void getMatchParticipants(@DestinationVariable Long realTimeMatchId) {
        Set<Long> participantsId = realTimeMatchService.getParticipantsId(new RealTimeMatchInfoRequest(realTimeMatchId));
        List<Member> participants = new ArrayList<>();
        for (Long id : participantsId) {
            participants.add(memberService.findByIdx(id));
        }
        messagingTemplate.convertAndSend("/topic/realtime-match/" + realTimeMatchId + "/participants", participants);
    }
}
