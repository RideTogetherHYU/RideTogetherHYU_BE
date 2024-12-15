package com.ridetogether.server.global.config;

import com.ridetogether.server.domain.chat.application.RedisSubscriber;
import com.ridetogether.server.domain.chat.dto.ChatMessageDto;
import com.ridetogether.server.domain.realtimematch.domain.RealTimeMatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    /*
    redis의 pub/sub 기능을 이용하기 위해 MessageListener 설정 추가
    메시지 발행이 오면 Listener가 처리함
     */
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    /*
    Redis 서버와의 연결을 생성하고 관리하는 데 사용
    */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapterForMatch
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // RedisMessageListenerContainer 에 사용할 Redis 연결 팩토리(RedisConnectionFactory)를 설정
        container.setConnectionFactory(connectionFactory);

        // match에 대한 리스너 추가
        container.addMessageListener(listenerAdapterForMatch, new ChannelTopic("realtime-match"));

        return container;
    }

    /*
    pub/sub 통신에 사용할 redisTemplate 설정
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return redisTemplate;
    }

    /*
    채팅 데이터(ChatMessageDto)를 저장할 때 사용할 RedisTemplate을 설정
    키는 문자열로 직렬화하고, 값은 ChatMessageDto 객체를 JSON 형식으로 직렬화
     */
    @Bean
    public RedisTemplate<Long, ChatMessageDto> redisTemplateForChatDto(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Long, ChatMessageDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessageDto.class)); // ChatDto 클래스를 직렬화
        return redisTemplate;
    }

    /*
    매칭 데이터를 저장할 때 사용할 RedisTemplate 설정
    키는 문자열로 직렬화, 값은 RealTimeMatchDto 객체를 Json 형식으로 직렬화
    * */
    @Bean
    public RedisTemplate<String, RealTimeMatch> redisTemplateForMatchDto(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RealTimeMatch> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(RealTimeMatch.class));
        return redisTemplate;
    }

    /*
      실제 메시지를 처리하는 subscriber 설정 추가
      sendMessage 라는 메소드가 -> RedisSubscriber 클래스 안에 오버라이딩 된 onMessage 로 처리하도록 함
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "sendMessage");
    }


}
