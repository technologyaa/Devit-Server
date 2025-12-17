package technologyaa.Devit.domain.websocketchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.exception.AuthErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.websocketchat.entity.ChatRoom;
import technologyaa.Devit.domain.websocketchat.repository.ChatRoomRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    /**
     * 채팅방 생성
     */
    public APIResponse<ChatRoomResponse> createRoom(ChatRoomRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Member creator = memberRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        ChatRoom room = ChatRoom.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .build();

        room.getMembers().add(creator);

        // 다른 멤버들 추가
        if (request.getMemberIds() != null) {
            request.getMemberIds().forEach(memberId -> {
                Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));
                room.getMembers().add(member);
            });
        }

        ChatRoom savedRoom = chatRoomRepository.save(room);
        return APIResponse.ok(ChatRoomResponse.from(savedRoom));
    }

    /**
     * 1:1 채팅방 생성 또는 조회
     */
    public APIResponse<ChatRoomResponse> getOrCreatePrivateRoom(Long otherMemberId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Member currentMember = memberRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        Member otherMember = memberRepository.findById(otherMemberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        // 기존 1:1 채팅방 찾기
        ChatRoom existingRoom = chatRoomRepository.findPrivateRoomBetweenUsers(
                currentMember.getUsername(), otherMember.getUsername()).orElse(null);

        if (existingRoom != null) {
            return APIResponse.ok(ChatRoomResponse.from(existingRoom));
        }

        // 새 1:1 채팅방 생성
        ChatRoom newRoom = ChatRoom.builder()
                .name(currentMember.getUsername() + " & " + otherMember.getUsername())
                .type(ChatRoom.RoomType.PRIVATE)
                .build();
        newRoom.getMembers().add(currentMember);
        newRoom.getMembers().add(otherMember);

        ChatRoom savedRoom = chatRoomRepository.save(newRoom);
        return APIResponse.ok(ChatRoomResponse.from(savedRoom));
    }

    /**
     * 내가 속한 채팅방 목록 조회
     */
    public APIResponse<List<ChatRoomResponse>> getMyRooms() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        List<ChatRoom> rooms = chatRoomRepository.findByMemberUsername(member.getUsername());
        List<ChatRoomResponse> responses = rooms.stream()
                .map(ChatRoomResponse::from)
                .collect(Collectors.toList());

        return APIResponse.ok(responses);
    }

    /**
     * 채팅방 상세 조회
     */
    public APIResponse<ChatRoomResponse> getRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        return APIResponse.ok(ChatRoomResponse.from(room));
    }

    /**
     * 채팅방에 멤버 추가
     */
    public APIResponse<ChatRoomResponse> addMemberToRoom(Long roomId, Long memberId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        if (room.getType() == ChatRoom.RoomType.PRIVATE) {
            throw new RuntimeException("1:1 채팅방에는 멤버를 추가할 수 없습니다.");
        }

        room.getMembers().add(member);
        ChatRoom savedRoom = chatRoomRepository.save(room);

        return APIResponse.ok(ChatRoomResponse.from(savedRoom));
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class ChatRoomRequest {
        private String name;
        private String description;
        private ChatRoom.RoomType type;
        private List<Long> memberIds;
    }

    @lombok.Getter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ChatRoomResponse {
        private Long id;
        private String name;
        private String description;
        private ChatRoom.RoomType type;
        private List<Long> memberIds;
        private java.time.LocalDateTime createdAt;

        public static ChatRoomResponse from(ChatRoom room) {
            List<Long> memberIds = room.getMembers().stream()
                    .map(Member::getId)
                    .collect(Collectors.toList());

            return ChatRoomResponse.builder()
                    .id(room.getId())
                    .name(room.getName())
                    .description(room.getDescription())
                    .type(room.getType())
                    .memberIds(memberIds)
                    .createdAt(room.getCreatedAt())
                    .build();
        }
    }
}

