package com.ridetogether.server.domain.matching.controller;

import com.ridetogether.server.domain.matching.application.MatchingService;
import com.ridetogether.server.domain.matching.converter.MatchingDtoConverter;
import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.ridetogether.server.domain.matching.dto.MatchingRequestDto.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;
    private final MemberService memberService;

    @PostMapping
    public ApiResponse<?> createMatching(@RequestBody CreateMatchingRequestDto requestDto) {
        String memberId = SecurityUtil.getLoginMemberId().orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Member loginMember = memberService.findByMemberId(memberId);
        return ApiResponse.onSuccess(matchingService.createMatching(MatchingDtoConverter.convertToCreateMatchingDto(requestDto, loginMember.getIdx())));
    }

//    @PostMapping("/join")
//    public ApiResponse<?> joinMatching(@RequestParam(value = "matchingIdx") Long matchingIdx) {
//        String memberId = SecurityUtil.getLoginMemberId().orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
//        Member loginMember = memberService.findByMemberId(memberId);
//        return ApiResponse.onSuccess(matchingService.joinMatching(matchingIdx, loginMember.getIdx()));
//    }

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
