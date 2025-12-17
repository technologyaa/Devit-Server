package technologyaa.Devit.domain.websocketchat.config;

import technologyaa.Devit.domain.websocketchat.dto.ChatMessage;
import technologyaa.Devit.domain.websocketchat.entity.ChatRoom;
import technologyaa.Devit.domain.websocketchat.repository.ChatMessageRepository;
import technologyaa.Devit.domain.websocketchat.repository.ChatRoomRepository;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ChatHandler extends TextWebSocketHandler {

    // 연결된 WebSocket 세션들을 저장하는 Set (Thread-safe)
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    // 세션 ID -> 사용자명 매핑
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();
    // 사용자명 -> 세션 ID 매핑 (한 사용자가 여러 세션을 가질 수 있음)
    private final Map<String, Set<String>> userSessionMap = new ConcurrentHashMap<>();
    
    private final ObjectMapper objectMapper;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatHandler(ObjectMapper objectMapper, ChatMessageRepository chatMessageRepository, 
                      ChatRoomRepository chatRoomRepository) {
        this.objectMapper = objectMapper;
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    // 1. 웹소켓 연결이 성공적으로 수립된 후 호출됩니다.
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        
        // 세션에서 사용자명 추출 (쿼리 파라미터 또는 헤더에서)
        String username = extractUsernameFromSession(session);
        if (username != null) {
            sessionUserMap.put(session.getId(), username);
            userSessionMap.computeIfAbsent(username, k -> ConcurrentHashMap.newKeySet()).add(session.getId());
            log.info("새 세션 연결됨: {}. 사용자: {}. 현재 세션 수: {}", session.getId(), username, sessions.size());
        } else {
            log.warn("세션 연결됨: {} (사용자명 없음)", session.getId());
        }
    }
    
    /**
     * WebSocket 세션에서 사용자명을 추출합니다.
     * URI 쿼리 파라미터에서 username을 추출합니다.
     */
    private String extractUsernameFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "username".equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

    // 2. 클라이언트로부터 메시지를 수신했을 때 호출됩니다.
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("수신 메시지 (세션 {}): {}", session.getId(), payload);

        ChatMessage chatMessage;
        try {
            // JSON 문자열을 ChatMessage 객체로 변환
            chatMessage = objectMapper.readValue(payload, ChatMessage.class);
            
            // 세션에서 사용자명 확인 (없으면 메시지의 sender 사용)
            String username = sessionUserMap.get(session.getId());
            if (username != null && chatMessage.getSender() == null) {
                chatMessage.setSender(username);
            }
            
            // 메시지 유효성 검증
            if (chatMessage.getSender() == null || chatMessage.getSender().trim().isEmpty()) {
                throw new IllegalArgumentException("메시지 발신자가 없습니다.");
            }
            if (chatMessage.getContent() == null || chatMessage.getContent().trim().isEmpty()) {
                throw new IllegalArgumentException("메시지 내용이 비어있습니다.");
            }

            // 채팅방 ID가 있는 경우 채팅방 조회 (클라이언트는 roomId 필드를 통해 전송)
            @SuppressWarnings("unchecked")
            Map<String, Object> messageMap = objectMapper.readValue(payload, Map.class);
            if (messageMap.containsKey("roomId") && messageMap.get("roomId") != null) {
                Long roomId = Long.valueOf(messageMap.get("roomId").toString());
                ChatRoom room = chatRoomRepository.findById(roomId).orElse(null);
                chatMessage.setRoom(room);
            }

            // 데이터베이스에 저장
            ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
            log.info("메시지 저장 완료. ID: {}, Room: {}", savedMessage.getId(), 
                    savedMessage.getRoom() != null ? savedMessage.getRoom().getId() : "null");

            // 메시지 전송 (채팅방 또는 1:1)
            sendMessage(savedMessage, session);

        } catch (IOException e) {
            log.error("메시지 처리 오류 또는 JSON 파싱 오류: {}", e.getMessage(), e);
            // 클라이언트에게 오류 알림
            try {
                String errorMessage = objectMapper.writeValueAsString(
                    Map.of("error", "Invalid message format.", "details", e.getMessage())
                );
                session.sendMessage(new TextMessage(errorMessage));
            } catch (IOException ex) {
                log.error("오류 메시지 전송 실패: {}", ex.getMessage());
            }
        }
    }
    
    /**
     * 메시지를 적절한 수신자들에게 전송합니다.
     * - receiver가 있으면 특정 사용자에게만 전송
     * - room이 있으면 해당 채팅방의 모든 멤버에게 전송
     * - 둘 다 없으면 브로드캐스트
     */
    private void sendMessage(ChatMessage message, WebSocketSession senderSession) {
        try {
            String messagePayload = objectMapper.writeValueAsString(message);
            TextMessage textMessage = new TextMessage(messagePayload);
            
            if (message.getReceiver() != null && !message.getReceiver().trim().isEmpty()) {
                // 1:1 메시지 전송
                sendToUser(message.getReceiver(), textMessage);
                sendToUser(message.getSender(), textMessage); // 발신자에게도 전송
            } else if (message.getRoom() != null) {
                // 채팅방 메시지 전송
                sendToRoom(message.getRoom(), textMessage, message.getSender());
            } else {
                // 브로드캐스트
                broadcastMessage(textMessage);
            }
        } catch (Exception e) {
            log.error("메시지 전송 중 오류: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 특정 사용자에게 메시지를 전송합니다.
     */
    private void sendToUser(String username, TextMessage message) {
        Set<String> sessionIds = userSessionMap.get(username);
        if (sessionIds != null) {
            sessionIds.forEach(sessionId -> {
                sessions.stream()
                    .filter(s -> s.getId().equals(sessionId) && s.isOpen())
                    .forEach(session -> {
                        try {
                            session.sendMessage(message);
                        } catch (IOException e) {
                            log.error("메시지 전송 오류 (세션 {}): {}", sessionId, e.getMessage());
                        }
                    });
            });
        }
    }
    
    /**
     * 채팅방의 모든 멤버에게 메시지를 전송합니다.
     */
    private void sendToRoom(ChatRoom room, TextMessage message, String sender) {
        if (room.getMembers() != null) {
            room.getMembers().forEach(member -> {
                String username = member.getUsername();
                if (!username.equals(sender)) { // 발신자는 제외
                    sendToUser(username, message);
                }
            });
        }
        // 발신자에게도 전송
        sendToUser(sender, message);
    }

    // 3. 웹소켓 연결이 닫힌 후 호출됩니다.
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        String username = sessionUserMap.remove(sessionId);
        
        if (username != null) {
            Set<String> userSessions = userSessionMap.get(username);
            if (userSessions != null) {
                userSessions.remove(sessionId);
                if (userSessions.isEmpty()) {
                    userSessionMap.remove(username);
                }
            }
            log.info("세션 연결 종료됨: {}. 사용자: {}. 현재 세션 수: {}, 종료 상태: {}", 
                    sessionId, username, sessions.size(), status.getCode());
        } else {
            log.info("세션 연결 종료됨: {}. 현재 세션 수: {}, 종료 상태: {}", 
                    sessionId, sessions.size(), status.getCode());
        }
        
        sessions.remove(session);
    }

    /**
     * 연결된 모든 세션에 메시지를 브로드캐스트합니다.
     * 
     * @param message 브로드캐스트할 메시지
     */
    private void broadcastMessage(TextMessage message) {
        final int[] counts = {0, 0}; // [성공, 실패]
        
        sessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                    counts[0]++;
                } catch (IOException e) {
                    log.error("메시지 전송 오류 (세션 {}): {}", session.getId(), e.getMessage());
                    counts[1]++;
                }
            }
        });
        
        if (counts[1] > 0) {
            log.warn("브로드캐스트 통계 - 성공: {}, 실패: {}", counts[0], counts[1]);
        }
    }

}
