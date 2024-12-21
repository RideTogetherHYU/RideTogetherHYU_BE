package com.ridetogether.server.domain.matching.controller;

import com.ridetogether.server.domain.matching.application.MatchingService;
import com.ridetogether.server.domain.matching.converter.MatchingDtoConverter;
import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.domain.matching.dto.MatchingResponseDto;
import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.realtimematch.converter.RealTimeMatchReqConverter;
import com.ridetogether.server.domain.realtimematch.domain.RealTimeMatch;
import com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchRequestDto;
import com.ridetogether.server.domain.realtimematch.service.RealTimeMatchService;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.ridetogether.server.domain.matching.dto.MatchingRequestDto.*;
import static com.ridetogether.server.domain.matching.dto.MatchingResponseDto.*;
import static com.ridetogether.server.domain.realtimematch.converter.RealTimeMatchReqConverter.*;
import static com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchRequestDto.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;
    private final RealTimeMatchService realTimeMatchService;
    private final MemberService memberService;

    @PostMapping
    public ApiResponse<?> createMatching(@RequestBody CreateMatchingRequestDto requestDto) {
        String memberId = SecurityUtil.getLoginMemberId().orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Member loginMember = memberService.findByMemberId(memberId);
        // create메소드 호출 -> realTimeMatch에서도 생성하기 위해서 바로 리턴 안 함
        CreateMatchingResponseDto createMatch = matchingService.createMatching(MatchingDtoConverter.convertToCreateMatchingDto(requestDto, loginMember.getIdx()));

        // realTimeMatch에서 create
        CreateRealTimeMatchRequestDto createRealTimeMatchRequestDto
                = convertCreateMatchDto(createMatch.getMatchingIdx(), requestDto.getMaxParticipantCnt(), LocalDateTime.parse(requestDto.getExpiredAt()));
        RealTimeMatch match = realTimeMatchService.createMatch(createRealTimeMatchRequestDto);
        matchingService.updateRealTimeMatchId(createMatch.getMatchingIdx(), match.getIdx());
        return ApiResponse.onSuccess(createMatch);
    }

//    @PostMapping("/join")
//    public ApiResponse<?> joinMatching(@RequestParam(value = "matchingIdx") Long matchingIdx) {
//        String memberId = SecurityUtil.getLoginMemberId().orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
//        Member loginMember = memberService.findByMemberId(memberId);
//        return ApiResponse.onSuccess(matchingService.joinMatching(matchingIdx, loginMember.getIdx()));
//    }

    @DeleteMapping
    public ApiResponse<?> deleteMatching(@RequestParam(value = "matchingIdx") Long matchingIdx) {
        String memberId = SecurityUtil.getLoginMemberId().orElseThrow(() -> new ErrorHandler(ErrorStatus.MATCHING_NOT_FOUND));
        Member member = memberService.findByMemberId(memberId);
        DeleteMatchingRequestDto dto = DeleteMatchingRequestDto.builder()
                .matchingIdx(matchingIdx)
                .hostMemberIdx(member.getIdx())
                .build();

        Matching matching = matchingService.findByIdx(matchingIdx);
        DeleteRealTimeMatchRequest deleteDto
                = DeleteRealTimeMatchRequest.builder()
                .realTimeMatchId(matching.getRealTimeMatchId())
                .participantId(member.getIdx())
                .build();

        realTimeMatchService.deleteMatch(deleteDto);
        return ApiResponse.onSuccess(matchingService.deleteMatching(dto));
    }

    @PatchMapping("/start")
    public ApiResponse<?> startMatching(@RequestParam(value = "matchingIdx") Long matchingIdx) {
        Matching matching = matchingService.findByIdx(matchingIdx);
        StartMatchingRequestDto reqDto = StartMatchingRequestDto.builder()
                .matchingIdx(matchingIdx)
                .realTimeMatchingIdx(matching.getRealTimeMatchId())
                .build();

        return ApiResponse.onSuccess(matchingService.startMatching(reqDto));
    }

    // 매칭 종료 후 가져오는 용도
    @GetMapping
    public ApiResponse<?> getMatchingInfo(@RequestParam(value = "matchingIdx") Long matchingIdx) {
        return ApiResponse.onSuccess(matchingService.getMatchingInfo(matchingIdx));
    }

//    @DeleteMapping("/join")
//    public ApiResponse<?> cancelJoinMatching(@RequestParam(value = "matchingIdx") Long matchingIdx) {
//        String memberId = SecurityUtil.getLoginMemberId().orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
//        Member loginMember = memberService.findByMemberId(memberId);
//        return ApiResponse.onSuccess(matchingService.cancelJoinMatching(matchingIdx, loginMember.getIdx()));
//    }

    @PostMapping("/cost")
    public ApiResponse<?> enterTaxiPrice(@RequestParam(value = "matchingIdx") Long matchingIdx,
                                         @RequestBody UpdatePriceRequestDto request) {
        return ApiResponse.onSuccess(matchingService.enterTaxiPrice(matchingIdx, request.getPrice()));
    }

}
