package com.ridetogether.server.domain.realtimematch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridetogether.server.domain.chat.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import static com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchRequestDto.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedisMatchPub {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(String topic, Object message) {
        redisTemplate.convertAndSend(topic, message);
    }
}
