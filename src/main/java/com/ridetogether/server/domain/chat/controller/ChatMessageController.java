package com.ridetogether.server.domain.chat.controller;

import com.ridetogether.server.domain.chat.application.ChatMessageService;
import com.ridetogether.server.domain.chat.application.RedisPublisher;
import com.ridetogether.server.domain.chat.application.RedisSubscriber;
import com.ridetogether.server.domain.chat.domain.ChatMessage;
import com.ridetogether.server.domain.chat.dto.ChatMessageDto;
import com.ridetogether.server.domain.chatroom.application.ChatRoomService;
import com.ridetogether.server.domain.chatroom.dao.RedisRepository;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatMessageController {

    private final RedisPublisher redisPublisher;
    private final SimpMessageSendingOperations template;
    private final ChatMessageService chatMessageService;

    private final ChatRoomService chatRoomService;
    private final RedisRepository redisRepository;
    private final MemberService memberService;

    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    private final RedisSubscriber redisSubscriber;

    // MessageMapping 을 통해 webSocket 로 들어오는 메시지를 발신 처리한다.
    // 이때 클라이언트에서는 /pub/chat/message 로 요청하게 되고 이것을 controller 가 받아서 처리한다.
    // 처리가 완료되면 /sub/chat/room/roomId 로 메시지가 전송된다.
    @MessageMapping("/chat/enter")
    public void enterUser(@Payload ChatMessageDto chatMessageDto) {

        String memberId = SecurityUtil.getLoginMemberId().orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Member loginMember = memberService.findByMemberId(memberId);
        // 채팅방 유저+1
        redisRepository.plusUserCnt(chatMessageDto.getChatRoomId());

        // 채팅방에 유저 추가
        ChannelTopic topic = redisRepository.enterChatRoom(loginMember.getIdx(), chatMessageDto.getChatRoomId());
        redisMessageListener.addMessageListener(redisSubscriber, topic);


        chatMessageDto.setMessage(chatMessageDto.getSenderNickName() + " 님 입장!!");
        log.info("{} 님 채팅방에 입장을 성공하였습니다. 채팅방 ID : {}", chatMessageDto.getSenderNickName(), chatMessageDto.getChatRoomId());
        template.convertAndSend("/sub/chat/room/" + chatMessageDto.getChatRoomId(), chatMessageDto);
    }

    // 해당 유저
    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatMessageDto chatMessageDto) {
        log.info("CHAT {}", chatMessageDto);
        ChatRoom chatRoom=chatRoomService.findRoomById(chatMessageDto.getChatRoomId());

        ChatMessage message = chatMessageService.createChatMessage(chatMessageDto, chatMessageDto.getSenderIdx());

//        chatRoomService.addChatMessage(chatRoom, message);
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        redisPublisher.publish(chatRoomService.getTopic(chatMessageDto.getChatRoomId() + ""), message);
        chatMessageService.saveMessage(chatMessageDto);
//        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);

    }
    // 대화 내역 조회
    @GetMapping("/chat/room/{roomId}/message")
    public ApiResponse<List<ChatMessageDto>> loadMessage(@PathVariable Long roomId) {
        return ApiResponse.onSuccess(chatMessageService.loadMessage(roomId));
    }
}
