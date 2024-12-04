package com.ridetogether.server.domain.chatroom.dao;

import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.matching.dao.MatchingRepository;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class RedisRepository {

    private final MatchingRepository matchingRepository;
    private final ChatRoomRepository chatRoomRepository;
    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, Long, ChatRoom> opsHashChatRoom;
    private HashOperations<String, String, Long> opsHashEnterInfo;
    // 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
    private Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        opsHashEnterInfo = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    /**
     * 채팅방 입장 : redis에 topic을 만들고 pub/sub 통신을 하기 위해 리스너를 설정한다.
     */
    public ChannelTopic enterChatRoom(Long memberIdx, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId).orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
        String chatRoomIdStr = chatRoomId + "";
        ChannelTopic topic = getTopic(chatRoomIdStr);
        if (topic == null) {
            log.info("기존에 등록된 topic이 없습니다. 새로운 topic 생성 : roomId = {}", chatRoomIdStr);
            topic = new ChannelTopic(chatRoomIdStr);
        }
        topics.put(chatRoomIdStr, topic);
        plusUserCnt(chatRoomId);
        opsHashChatRoom.put(CHAT_ROOMS, memberIdx, chatRoom);

        return topic;
    }

    public ChannelTopic getTopic(String roomId) {
        log.info("topic 을 불러옵니다 : roomId = {}, topic = {}", roomId, topics.get(roomId));
        return topics.get(roomId);
    }

    public void plusUserCnt(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId).orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
        chatRoom.plusUserCount();
//        chatRoomRepository.save(chatRoom);
    }

    public void minusUserCnt(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId).orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
        if (chatRoom.getUserCount() != 0) {
            chatRoom.minusUserCount();
        }
//        chatRoomRepository.save(chatRoom);
    }

    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void setMemberEnterInfo(String sessionId, Long chatRoomId) {
        opsHashEnterInfo.put(ENTER_INFO, sessionId, chatRoomId);
    }

    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public Long getMemberEnteredChatRoomId(String sessionId) {
        Long memberIdx = opsHashEnterInfo.get(ENTER_INFO, sessionId);
        if (memberIdx == null) {
            throw new ErrorHandler(ErrorStatus.CHAT_ROOM_MEMBER_NOT_FOUND);
        }
        ChatRoom chatRoom = opsHashChatRoom.get(CHAT_ROOMS, memberIdx);
        if (chatRoom == null) {
            throw new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND);
        }
        return chatRoom.getChatRoomId();
    }

    // 사용자가 특정 채팅방에 입장해 있는지 확인
    public boolean existMemberInChatRoom(Long chatRoomId, String sessionId) {
        return getMemberEnteredChatRoomId(sessionId).equals(chatRoomId);
    }

    // 사용자 퇴장
    public void exitMemberEnterChatRoom(Long memberIdx) {
        opsHashEnterInfo.delete(CHAT_ROOMS, memberIdx);
    }

    // 나의 대화상대 정보 저장
    public void saveMyInfo(String sessionId, Long memberIdx) {
        opsHashEnterInfo.put(ENTER_INFO, sessionId, memberIdx);
    }

    public boolean existMyInfo(String sessionId) {
        return opsHashEnterInfo.hasKey(ENTER_INFO, sessionId);
    }

    public Long getMyInfo(String sessionId) {
        return opsHashEnterInfo.get(ENTER_INFO, sessionId);
    }

    // 나의 대화상대 정보 삭제
    public void deleteMyInfo(String sessionId) {
        opsHashEnterInfo.delete(ENTER_INFO, sessionId);
    }

}
