package com.example.websocketchat.dto; // 패키지 경로를 dto 아래로 변경

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor; // 기본 생성자 어노테이션 추가
import lombok.AllArgsConstructor; // 모든 인수를 받는 생성자도 추가 (선택적)
import jakarta.persistence.*; // JPA 관련 어노테이션 임포트
import java.time.LocalDateTime;

// Lombok 사용 시 getter/setter, 생성자 등을 자동으로 생성해줍니다.
@Entity // 🚨 데이터베이스 엔티티임을 명시
@Table(name = "chat_message") // 테이블 이름 지정 (선택적)
@Getter
@Setter
@NoArgsConstructor // JSON 역직렬화(Deserialization)에 필수
@AllArgsConstructor // 모든 필드를 포함하는 생성자 추가 (편의상 추가)
public class ChatMessage {

    @Id // 🚨 기본 키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 ID를 자동 생성하도록 설정
    private Long id; // ID 필드 추가

    // 클라이언트가 보내는 JSON의 키와 이 필드 이름이 정확히 일치해야 합니다.
    private String sender;
    private String content; // 클라이언트의 JSON 필드명과 일치시킵니다.

    @Enumerated(EnumType.STRING) // Enum을 DB에 문자열로 저장
    private MessageType type; // 메시지 타입 (예: ENTER, TALK, LEAVE)

    // 메시지 생성 시간 저장
    private LocalDateTime timestamp = LocalDateTime.now();

    public enum MessageType {
        ENTER, TALK, LEAVE
    }
}
