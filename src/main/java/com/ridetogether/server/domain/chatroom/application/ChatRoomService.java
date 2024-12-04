package com.ridetogether.server.domain.chatroom.application;

import com.ridetogether.server.domain.chat.application.RedisSubscriber;
import com.ridetogether.server.domain.chat.domain.ChatMessage;
import com.ridetogether.server.domain.chat.model.ChatStatus;
import com.ridetogether.server.domain.chatroom.converter.ChatRoomDtoConverter;
import com.ridetogether.server.domain.chatroom.dao.ChatRoomRepository;
import com.ridetogether.server.domain.chatroom.dao.RedisRepository;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.matching.dao.MatchingRepository;
import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.ridetogether.server.domain.chatroom.dto.ChatRoomResponseDto.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {

    private final MatchingRepository matchingRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisRepository redisRepository;
    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;

    //채팅방
    //방 아이디로 검색
//    @Cacheable(value = "ChatRoom", cacheManager = "testCacheManager")
    public List<ChatRoom> findAllRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        Collections.reverse(chatRooms);
        log.info(String.valueOf(chatRooms.size()));
        return chatRooms;
//      return  opsHashChatRoom.values(CHAT_ROOMS);
    }

    public ChatRoom findRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
    }

    public void addChatMessage(ChatRoom chatRoom, ChatMessage chatMessage) {
        chatRoom.addChatMessage(chatMessage);
    }

    /**
     * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
     */
    public CreateChatRoomResponseDto createChatRoom(Long chatRoomId, Long memberIdx) {
        Matching matching = matchingRepository.findByIdx(chatRoomId).orElseThrow(() -> new ErrorHandler(ErrorStatus.MATCHING_NOT_FOUND));

        if (chatRoomRepository.existsByChatRoomId(chatRoomId)) {
            throw new ErrorHandler(ErrorStatus.CHAT_ROOM_ALREADY_EXIST);
        } else {
            ChatRoom chatRoom = ChatRoom.builder()
                    .chatRoomId(chatRoomId)
                    .matching(matching)
                    .userCount(0)
                    .chatStatus(ChatStatus.ACTIVE)
                    .build();
            chatRoomRepository.save(chatRoom);
            ChannelTopic topic = redisRepository.enterChatRoom(memberIdx, chatRoomId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            log.info("채팅방 생성 : {} 번 방", chatRoomId);
            return ChatRoomDtoConverter.convertToCreateChatRoomResponseDto(chatRoom);
        }

    }

    public ChannelTopic getTopic(String roomId) {
        return redisRepository.getTopic(roomId);
    }

}