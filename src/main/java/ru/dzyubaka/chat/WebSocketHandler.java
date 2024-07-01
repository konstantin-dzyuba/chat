package ru.dzyubaka.chat;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final RedisMessageSubscriber redisMessageSubscriber;
    private final StringRedisTemplate redisTemplate;

    public WebSocketHandler(RedisMessageSubscriber redisMessageSubscriber, StringRedisTemplate redisTemplate) {
        this.redisMessageSubscriber = redisMessageSubscriber;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        redisMessageSubscriber.addSession(session);
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        redisTemplate.convertAndSend("websocket-channel", payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        redisMessageSubscriber.removeSession(session);
        super.afterConnectionClosed(session, status);
    }
}
