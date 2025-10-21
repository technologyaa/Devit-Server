package com.example.websocketchat.repository;

import com.example.websocketchat.dto.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ChatMessage 엔티티와 Long 타입의 ID를 사용하는 JpaRepository
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 추가적인 사용자 정의 쿼리가 필요하면 여기에 정의합니다.
}
