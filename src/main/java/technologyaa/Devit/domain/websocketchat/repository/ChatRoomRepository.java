package technologyaa.Devit.domain.websocketchat.repository;

import technologyaa.Devit.domain.websocketchat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    // 특정 사용자가 속한 채팅방 목록 조회 (members를 fetch join으로 함께 조회)
    @Query("SELECT DISTINCT r FROM ChatRoom r JOIN FETCH r.members m WHERE m.username = :username")
    List<ChatRoom> findByMemberUsername(@Param("username") String username);
    
    // 두 사용자 간의 1:1 채팅방 찾기 (members를 fetch join으로 함께 조회)
    @Query("SELECT r FROM ChatRoom r JOIN FETCH r.members WHERE r.type = 'PRIVATE' AND " +
           "EXISTS (SELECT m1 FROM r.members m1 WHERE m1.username = :username1) AND " +
           "EXISTS (SELECT m2 FROM r.members m2 WHERE m2.username = :username2) AND " +
           "SIZE(r.members) = 2")
    Optional<ChatRoom> findPrivateRoomBetweenUsers(@Param("username1") String username1, 
                                                    @Param("username2") String username2);
    
    // ID로 채팅방 조회 (members를 fetch join으로 함께 조회)
    @Query("SELECT r FROM ChatRoom r LEFT JOIN FETCH r.members WHERE r.id = :id")
    Optional<ChatRoom> findByIdWithMembers(@Param("id") Long id);
}

