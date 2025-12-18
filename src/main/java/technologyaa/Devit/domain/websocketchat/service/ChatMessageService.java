package technologyaa.Devit.domain.websocketchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.exception.AuthErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.websocketchat.dto.ChatMessage;
import technologyaa.Devit.domain.websocketchat.entity.ChatRoom;
import technologyaa.Devit.domain.websocketchat.repository.ChatMessageRepository;
import technologyaa.Devit.domain.websocketchat.repository.ChatRoomRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    /**
     * 채팅방의 메시지 목록 조회
     */
    public APIResponse<List<ChatMessage>> getRoomMessages(Long roomId, int page, int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        ChatRoom room = chatRoomRepository.findByIdWithMembers(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // 채팅방 멤버 확인
        boolean isMember = room.getMembers().stream()
                .anyMatch(m -> m.getUsername().equals(member.getUsername()));
        if (!isMember) {
            throw new RuntimeException("채팅방 멤버가 아닙니다.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<ChatMessage> messagePage = chatMessageRepository.findByRoom_IdOrderByTimestampDesc(roomId, pageable);
        List<ChatMessage> messages = messagePage.getContent();
        
        // 최신순으로 정렬되어 있으므로 오래된 순으로 역순 정렬
        java.util.Collections.reverse(messages);

        return APIResponse.ok(messages);
    }

    /**
     * 1:1 채팅 메시지 조회
     */
    public APIResponse<List<ChatMessage>> getConversationMessages(String otherUsername) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Member currentMember = memberRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        List<ChatMessage> messages = chatMessageRepository.findConversationBetweenUsers(
                currentMember.getUsername(), otherUsername);

        return APIResponse.ok(messages);
    }
}

