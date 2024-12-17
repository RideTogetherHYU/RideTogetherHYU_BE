package com.ridetogether.server.domain.chatroom.controller;

import com.ridetogether.server.domain.chat.application.ChatMessageService;
import com.ridetogether.server.domain.chatroom.application.ChatRoomService;
import com.ridetogether.server.domain.chatroom.converter.ChatRoomDtoConverter;
import com.ridetogether.server.domain.chatroom.dto.ChatRoomResponseDto.CreateChatRoomResponseDto;
import com.ridetogether.server.domain.chatroom.dto.ChatRoomResponseDto.GetChatRoomResponseDto;
import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final MemberService memberService;

    @GetMapping("/room/all")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<GetChatRoomResponseDto>> getAllChatRoom() {
        List<GetChatRoomResponseDto> responseDtos = chatRoomService.findAllRooms().stream()
                .map(ChatRoomDtoConverter::convertToGetChatRoomResponseDto)
                .toList();
        return ApiResponse.onSuccess(responseDtos);
    }

    @PostMapping("/room/{matchingIdx}")
    public ApiResponse<CreateChatRoomResponseDto> createRoom(@PathVariable(value = "matchingIdx") Long matchingIdx) {
        String memberId = SecurityUtil.getLoginMemberId().orElseThrow(() -> new ErrorHandler(ErrorStatus._UNAUTHORIZED));
        Member member = memberService.findByMemberId(memberId);
        return ApiResponse.onSuccess(chatRoomService.    createChatRoom(matchingIdx, member.getIdx()));
    }

//    @ApiOperation(value = "방 정보 보기", notes = "방 정보")
//    @GetMapping("/room/{roomId}")
//    public SingleResult<ChatRoom> roomInfo(@PathVariable String roomId) {
//        return responseService.getSingleResult(chatService.findRoomById(roomId));
//    }
//
//    @ApiOperation(value = "customer 별 방 조회")
//    @GetMapping("/customer")
//    public ListResult<ChatRoom> getRoomsByCustomer(@RequestHeader("X-AUTH-TOKEN") String xAuthToken){
//        UserIdDto customer=userServiceClient.getUserId(xAuthToken);
//        return responseService.getListResult(chatService.getCustomerEnterRooms(customer));
//    }
//
//    @ApiOperation(value = "store 별 방 조회")
//    @GetMapping("/store")
//    public ListResult<ChatRoom> getRoomsByStore(@RequestHeader("X-AUTH-TOKEN") String xAuthToken){
//        UserIdDto store=userServiceClient.getUs@ApiOperation(value = "채팅방 개설", notes = "채팅방을 개설한다.")
//        @PostMapping("/room")
//        public SingleResult<ChatRoom> createRoom(@RequestHeader("X-AUTH-TOKEN") String xAuthToken,@RequestBody UserIdDto store) {
//            UserIdDto customer = userServiceClient.getUserId(xAuthToken);
//            return responseService.getSingleResult(chatService.createChatRoom(customer,store));
//        }
//
//        @ApiOperation(value = "방 정보 보기", notes = "방 정보")
//        @GetMapping("/room/{roomId}")
//        public SingleResult<ChatRoom> roomInfo(@PathVariable String roomId) {
//            return responseService.getSingleResult(chatService.findRoomById(roomId));
//        }
//
//        @ApiOperation(value = "customer 별 방 조회")
//        @GetMapping("/customer")
//        public ListResult<ChatRoom> getRoomsByCustomer(@RequestHeader("X-AUTH-TOKEN") String xAuthToken){
//            UserIdDto customer=userServiceClient.getUserId(xAuthToken);
//            return responseService.getListResult(chatService.getCustomerEnterRooms(customer));
//        }
//
//        @ApiOperation(value = "store 별 방 조회")
//        @GetMapping("/store")
//        public ListResult<ChatRoom> getRoomsByStore(@RequestHeader("X-AUTH-TOKEN") String xAuthToken){
//            UserIdDto store=userServiceClient.getUserId(xAuthToken);
//            return responseService.getListResult(chatService.getCustomerEnterRooms(store));
//        }erId(xAuthToken);
//        return responseService.getListResult(chatService.getCustomerEnterRooms(store));
//    }
}
