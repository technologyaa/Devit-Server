package technologyaa.Devit.domain.websocketchat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.exception.AuthErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.websocketchat.dto.ChatMessage;
import technologyaa.Devit.domain.websocketchat.repository.ChatMessageRepository;
import technologyaa.Devit.domain.websocketchat.repository.ChatRoomRepository;
import technologyaa.Devit.global.util.SecurityUtil;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SecurityUtil securityUtil;

    /**
     * 채팅방의 메시지 목록 조회
     */
    public APIResponse<List<ChatMessage>> getRoomMessages(Long roomId, int page, int size) {
        try {
            log.info("채팅방 메시지 조회 시작 - roomId: {}, page: {}, size: {}", roomId, page, size);
            
            Member member = securityUtil.getMember();
            log.info("인증된 사용자: {}", member.getUsername());

            // 채팅방 존재 확인 및 멤버 권한 확인 (한 번의 쿼리로 처리)
            boolean isMember = chatRoomRepository.existsByRoomIdAndMemberUsername(roomId, member.getUsername());
            if (!isMember) {
                log.warn("채팅방을 찾을 수 없거나 멤버가 아닌 사용자의 조회 시도 - roomId: {}, member: {}", roomId, member.getUsername());
                throw new AuthException(AuthErrorCode.FORBIDDEN);
            }
            
            log.info("채팅방 권한 확인 완료 - roomId: {}, member: {}", roomId, member.getUsername());

            Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
            Page<ChatMessage> messagePage = chatMessageRepository.findByRoom_IdOrderByTimestampDesc(roomId, pageable);
            List<ChatMessage> messages = new java.util.ArrayList<>(messagePage.getContent());
            log.info("메시지 조회 완료 - 총 메시지 수: {}", messages.size());
            
            // 최신순으로 정렬되어 있으므로 오래된 순으로 역순 정렬
            java.util.Collections.reverse(messages);

            // 각 메시지의 roomId를 명시적으로 설정 (JSON 직렬화를 위해)
            // room 객체는 LAZY이므로 직접 접근하지 않고, 파라미터로 받은 roomId 사용
            messages.forEach(msg -> msg.setRoomId(roomId));

            log.info("채팅방 메시지 조회 완료 - roomId: {}, 반환할 메시지 수: {}", roomId, messages.size());
            return APIResponse.ok(messages);
        } catch (AuthException e) {
            log.error("인증 오류 - roomId: {}", roomId, e);
            throw e;
        } catch (RuntimeException e) {
            log.error("런타임 오류 - roomId: {}, 오류 메시지: {}", roomId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("예상치 못한 오류 - roomId: {}", roomId, e);
            throw new RuntimeException("메시지 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 1:1 채팅 메시지 조회
     */
    public APIResponse<List<ChatMessage>> getConversationMessages(String otherUsername) {
        try {
            Member currentMember = securityUtil.getMember();
            log.info("1:1 채팅 메시지 조회 요청 - currentUser: {}, otherUser: {}", 
                    currentMember.getUsername(), otherUsername);

            List<ChatMessage> messages = chatMessageRepository.findConversationBetweenUsers(
                    currentMember.getUsername(), otherUsername);

            log.info("1:1 채팅 메시지 조회 완료 - 메시지 수: {}", messages.size());
            return APIResponse.ok(messages);
        } catch (AuthException e) {
            log.error("인증 오류 - otherUsername: {}", otherUsername, e);
            throw e;
        } catch (Exception e) {
            log.error("1:1 채팅 메시지 조회 오류 - otherUsername: {}", otherUsername, e);
            throw new RuntimeException("메시지 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}

