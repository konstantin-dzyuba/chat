package ru.dzyubaka.chat;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RedisMessageSubscriber extends MessageListenerAdapter {

    private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message.getBody()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }
}
