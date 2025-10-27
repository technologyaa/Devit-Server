package com.example.websocketchat.config;

import com.example.websocketchat.dto.ChatMessage;
import com.example.websocketchat.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ChatHandler extends TextWebSocketHandler {

    // 연결된 WebSocket 세션들을 저장하는 Set (Thread-safe)
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final ObjectMapper objectMapper;
    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatHandler(ObjectMapper objectMapper, ChatMessageRepository chatMessageRepository) {
        this.objectMapper = objectMapper;
        this.chatMessageRepository = chatMessageRepository;
    }

    // 1. 웹소켓 연결이 성공적으로 수립된 후 호출됩니다.
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("새 세션 연결됨: {}. 현재 세션 수: {}", session.getId(), sessions.size());

        // 연결 시 기존 메시지를 불러와 전송 (옵션)
        loadAndSendPreviousMessages(session);
    }

    // 2. 클라이언트로부터 메시지를 수신했을 때 호출됩니다.
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("수신 메시지 (세션 {}): {}", session.getId(), payload);

        ChatMessage chatMessage;
        try {
            // JSON 문자열을 ChatMessage 객체로 변환
            // 클라이언트는 {"sender": "사용자이름", "content": "메시지 내용"} 형태의 JSON을 전송해야 합니다.
            chatMessage = objectMapper.readValue(payload, ChatMessage.class);

            // 데이터베이스에 저장
            ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
            log.info("메시지 저장 완료. ID: {}", savedMessage.getId());

            // 저장된 메시지를 다시 JSON 문자열로 변환하여 브로드캐스트
            String broadcastPayload = objectMapper.writeValueAsString(savedMessage);
            broadcastMessage(new TextMessage(broadcastPayload));

        } catch (IOException e) {
            log.error("메시지 처리 오류 또는 JSON 파싱 오류: {}", e.getMessage());
            // 클라이언트에게 오류 알림 (옵션)
            session.sendMessage(new TextMessage("{\"error\": \"Invalid message format.\"}"));
        }
    }

    // 3. 웹소켓 연결이 닫힌 후 호출됩니다.
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("세션 연결 종료됨: {}. 현재 세션 수: {}", session.getId(), sessions.size());
    }

    /**
     * 연결된 모든 세션에 메시지를 브로드캐스트합니다.
     * 
     * @param message 브로드캐스트할 메시지
     */
    private void broadcastMessage(TextMessage message) {
        sessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    log.error("메시지 전송 오류 (세션 {}): {}", session.getId(), e.getMessage());
                }
            }
        });
    }

    /**
     * 이전 메시지를 불러와 특정 세션에 전송합니다.
     * 모든 메시지를 오래된 순서대로 정렬하여 클라이언트에 전송합니다.
     * 
     * @param session 메시지를 전송할 WebSocket 세션
     */
    private void loadAndSendPreviousMessages(WebSocketSession session) {
        try {
            log.info("📚 채팅 히스토리 로딩 시작...");
            
            // 모든 메시지를 불러와서 오래된 순서대로 정렬 (맨 아래에 최신 메시지가 보이도록)
            List<ChatMessage> messages = chatMessageRepository.findAll().stream()
                    .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp())) // 오래된 순서
                    .collect(Collectors.toList());
            
            log.info("📚 히스토리 메시지 개수: {}", messages.size());
            
            for (ChatMessage message : messages) {
                try {
                    String historyPayload = objectMapper.writeValueAsString(message);
                    session.sendMessage(new TextMessage(historyPayload));
                } catch (Exception e) {
                    log.error("이전 메시지 전송 오류 (ID: {}): {}", message.getId(), e.getMessage(), e);
                }
            }
            
            log.info("📚 채팅 히스토리 로딩 완료!");
        } catch (Exception e) {
            log.error("이전 메시지 로드 중 오류 발생: {}", e.getMessage());
        }
    }
}
