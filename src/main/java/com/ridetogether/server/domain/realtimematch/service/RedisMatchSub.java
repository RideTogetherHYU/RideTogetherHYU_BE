package com.ridetogether.server.domain.realtimematch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridetogether.server.domain.realtimematch.domain.RealTimeMatch;
import com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import static com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchRequestDto.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedisMatchSub implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // redis에서 발행된 데이터를 받아 역직렬화
            String pubMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());
            // 객체로 매핑
            EnterAndLeaveMatchRequest realTimeMatch = objectMapper.readValue(pubMessage, EnterAndLeaveMatchRequest.class);
            // 구독자에게 전송
            messagingTemplate.convertAndSend("/sub/realtime-match/" + realTimeMatch.getRealTimeMatchId());

        } catch (Exception e) {
            log.error("Exception : {}", e.getMessage());
        }
    }
}
