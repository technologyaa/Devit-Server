package technologyaa.Devit.domain.websocketchat.repository;

import technologyaa.Devit.domain.websocketchat.dto.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ChatMessage 엔티티와 Long 타입의 ID를 사용하는 JpaRepository
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 페이징을 위한 쿼리 메서드
    org.springframework.data.domain.Page<ChatMessage> findAllByOrderByTimestampDesc(
            org.springframework.data.domain.Pageable pageable);
    
    // 특정 시간 이후 메시지 조회
    java.util.List<ChatMessage> findByTimestampAfterOrderByTimestampAsc(
            java.time.LocalDateTime timestamp);
    
    // 특정 유저가 보낸 메시지 조회
    java.util.List<ChatMessage> findBySender(String sender);
    
    // 고유한 sender 목록 조회
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c.sender FROM ChatMessage c")
    java.util.List<String> findDistinctSenders();
}
