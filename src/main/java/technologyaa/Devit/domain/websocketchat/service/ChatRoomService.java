package technologyaa.Devit.domain.websocketchat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.exception.AuthErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.websocketchat.entity.ChatRoom;
import technologyaa.Devit.domain.websocketchat.repository.ChatRoomRepository;
import technologyaa.Devit.global.util.SecurityUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;

    /**
     * 채팅방 생성
     */
    public APIResponse<ChatRoomResponse> createRoom(ChatRoomRequest request) {
        try {
            log.info("채팅방 생성 요청: {}", request);
            
            if (request == null) {
                log.error("채팅방 생성 요청이 null입니다.");
                throw new IllegalArgumentException("요청이 null입니다.");
            }
            
            Member creator = securityUtil.getMember();
            log.debug("채팅방 생성자: {}", creator.getUsername());

            // PRIVATE 타입 채팅방 처리
            if (request.getType() == ChatRoom.RoomType.PRIVATE || 
                (request.getType() == null && request.getMemberIds() != null && request.getMemberIds().size() == 1)) {
                
                List<Long> memberIds = request.getMemberIds();
                if (memberIds == null || memberIds.isEmpty()) {
                    log.error("1:1 채팅방 생성 시 상대방 ID가 필요합니다.");
                    throw new IllegalArgumentException("1:1 채팅방 생성 시 상대방 ID가 필요합니다.");
                }
                
                Long otherMemberId = memberIds.get(0);
                
                // 자기 자신과 채팅방을 만들 수 없음
                if (creator.getId().equals(otherMemberId)) {
                    log.error("자기 자신과 채팅방을 생성할 수 없습니다. creatorId: {}, otherMemberId: {}", 
                            creator.getId(), otherMemberId);
                    throw new IllegalArgumentException("자기 자신과 채팅방을 생성할 수 없습니다.");
                }
                
                // 기존 1:1 채팅방이 있는지 확인
                Member otherMember = memberRepository.findById(otherMemberId)
                        .orElseThrow(() -> {
                            log.error("상대방을 찾을 수 없습니다. memberId: {}", otherMemberId);
                            return new AuthException(AuthErrorCode.MEMBER_NOT_FOUND);
                        });
                
                ChatRoom existingRoom = chatRoomRepository.findPrivateRoomBetweenUsers(
                        creator.getUsername(), otherMember.getUsername()).orElse(null);
                
                if (existingRoom != null) {
                    log.info("기존 1:1 채팅방 반환. roomId: {}", existingRoom.getId());
                    return APIResponse.ok(ChatRoomResponse.from(existingRoom));
                }
                
                // 새 1:1 채팅방 생성
                String roomName = request.getName();
                if (roomName == null || roomName.trim().isEmpty()) {
                    roomName = creator.getUsername() + " & " + otherMember.getUsername();
                }
                
                ChatRoom newRoom = ChatRoom.builder()
                        .name(roomName)
                        .description(request.getDescription() != null ? request.getDescription() : "")
                        .type(ChatRoom.RoomType.PRIVATE)
                        .build();
                newRoom.getMembers().add(creator);
                newRoom.getMembers().add(otherMember);
                
                ChatRoom savedRoom = chatRoomRepository.save(newRoom);
                log.info("새 1:1 채팅방 생성 완료. roomId: {}", savedRoom.getId());
                return APIResponse.ok(ChatRoomResponse.from(savedRoom));
            }

            // 그룹 채팅방 처리
            String roomName = request.getName();
            if (roomName == null || roomName.trim().isEmpty()) {
                log.error("그룹 채팅방 이름은 필수입니다.");
                throw new IllegalArgumentException("채팅방 이름은 필수입니다.");
            }

            ChatRoom.RoomType roomType = request.getType();
            if (roomType == null) {
                roomType = ChatRoom.RoomType.GROUP;
            }

            ChatRoom room = ChatRoom.builder()
                    .name(roomName)
                    .description(request.getDescription() != null ? request.getDescription() : "")
                    .type(roomType)
                    .build();

            room.getMembers().add(creator);

            // 다른 멤버들 추가 (중복 제거)
            Set<Long> addedMemberIds = new HashSet<>();
            addedMemberIds.add(creator.getId());
            
            if (request.getMemberIds() != null) {
                request.getMemberIds().forEach(memberId -> {
                    if (!addedMemberIds.contains(memberId)) {
                        Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> {
                                    log.error("멤버를 찾을 수 없습니다. memberId: {}", memberId);
                                    return new AuthException(AuthErrorCode.MEMBER_NOT_FOUND);
                                });
                        room.getMembers().add(member);
                        addedMemberIds.add(memberId);
                    }
                });
            }

            ChatRoom savedRoom = chatRoomRepository.save(room);
            log.info("그룹 채팅방 생성 완료. roomId: {}", savedRoom.getId());
            return APIResponse.ok(ChatRoomResponse.from(savedRoom));
            
        } catch (IllegalArgumentException | AuthException e) {
            log.error("채팅방 생성 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("채팅방 생성 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("채팅방 생성 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 1:1 채팅방 생성 또는 조회
     */
    public APIResponse<ChatRoomResponse> getOrCreatePrivateRoom(Long otherMemberId) {
        try {
            log.info("1:1 채팅방 조회/생성 요청. otherMemberId: {}", otherMemberId);
            
            Member currentMember = securityUtil.getMember();
            log.debug("현재 사용자: {}", currentMember.getUsername());

            // 자기 자신과 채팅방을 만들 수 없음
            if (currentMember.getId().equals(otherMemberId)) {
                log.error("자기 자신과 채팅방을 생성할 수 없습니다. currentMemberId: {}, otherMemberId: {}", 
                        currentMember.getId(), otherMemberId);
                throw new IllegalArgumentException("자기 자신과 채팅방을 생성할 수 없습니다.");
            }

            Member otherMember = memberRepository.findById(otherMemberId)
                    .orElseThrow(() -> {
                        log.error("상대방을 찾을 수 없습니다. memberId: {}", otherMemberId);
                        return new AuthException(AuthErrorCode.MEMBER_NOT_FOUND);
                    });

            // 기존 1:1 채팅방 찾기
            ChatRoom existingRoom = chatRoomRepository.findPrivateRoomBetweenUsers(
                    currentMember.getUsername(), otherMember.getUsername()).orElse(null);

            if (existingRoom != null) {
                log.info("기존 1:1 채팅방 반환. roomId: {}", existingRoom.getId());
                return APIResponse.ok(ChatRoomResponse.from(existingRoom));
            }

            // 새 1:1 채팅방 생성
            ChatRoom newRoom = ChatRoom.builder()
                    .name(currentMember.getUsername() + " & " + otherMember.getUsername())
                    .description("")
                    .type(ChatRoom.RoomType.PRIVATE)
                    .build();
            newRoom.getMembers().add(currentMember);
            newRoom.getMembers().add(otherMember);

            ChatRoom savedRoom = chatRoomRepository.save(newRoom);
            log.info("새 1:1 채팅방 생성 완료. roomId: {}", savedRoom.getId());
            return APIResponse.ok(ChatRoomResponse.from(savedRoom));
            
        } catch (IllegalArgumentException | AuthException e) {
            log.error("1:1 채팅방 조회/생성 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("1:1 채팅방 조회/생성 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("1:1 채팅방 조회/생성 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 내가 속한 채팅방 목록 조회
     */
    @Transactional(readOnly = true)
    public APIResponse<List<ChatRoomResponse>> getMyRooms() {
        try {
            Member member = securityUtil.getMember();
            log.debug("내 채팅방 목록 조회. username: {}", member.getUsername());

            List<ChatRoom> rooms = chatRoomRepository.findByMemberUsername(member.getUsername());
            List<ChatRoomResponse> responses = rooms.stream()
                    .map(ChatRoomResponse::from)
                    .collect(Collectors.toList());

            log.info("채팅방 {}개 조회 완료", responses.size());
            return APIResponse.ok(responses);
            
        } catch (Exception e) {
            log.error("내 채팅방 목록 조회 중 오류 발생", e);
            throw new RuntimeException("채팅방 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 채팅방 상세 조회
     */
    @Transactional(readOnly = true)
    public APIResponse<ChatRoomResponse> getRoom(Long roomId) {
        try {
            log.debug("채팅방 상세 조회. roomId: {}", roomId);
            
            ChatRoom room = chatRoomRepository.findByIdWithMembers(roomId)
                    .orElseThrow(() -> {
                        log.error("채팅방을 찾을 수 없습니다. roomId: {}", roomId);
                        return new RuntimeException("채팅방을 찾을 수 없습니다.");
                    });

            return APIResponse.ok(ChatRoomResponse.from(room));
            
        } catch (RuntimeException e) {
            log.error("채팅방 상세 조회 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("채팅방 상세 조회 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("채팅방 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 채팅방에 멤버 추가
     */
    public APIResponse<ChatRoomResponse> addMemberToRoom(Long roomId, Long memberId) {
        try {
            log.info("채팅방에 멤버 추가. roomId: {}, memberId: {}", roomId, memberId);
            
            ChatRoom room = chatRoomRepository.findByIdWithMembers(roomId)
                    .orElseThrow(() -> {
                        log.error("채팅방을 찾을 수 없습니다. roomId: {}", roomId);
                        return new RuntimeException("채팅방을 찾을 수 없습니다.");
                    });

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("멤버를 찾을 수 없습니다. memberId: {}", memberId);
                        return new AuthException(AuthErrorCode.MEMBER_NOT_FOUND);
                    });

            if (room.getType() == ChatRoom.RoomType.PRIVATE) {
                log.error("1:1 채팅방에는 멤버를 추가할 수 없습니다. roomId: {}", roomId);
                throw new IllegalArgumentException("1:1 채팅방에는 멤버를 추가할 수 없습니다.");
            }

            if (room.getMembers().contains(member)) {
                log.warn("이미 채팅방에 포함된 멤버입니다. roomId: {}, memberId: {}", roomId, memberId);
                return APIResponse.ok(ChatRoomResponse.from(room));
            }

            room.getMembers().add(member);
            ChatRoom savedRoom = chatRoomRepository.save(room);
            log.info("채팅방에 멤버 추가 완료. roomId: {}, memberId: {}", roomId, memberId);

            return APIResponse.ok(ChatRoomResponse.from(savedRoom));
            
        } catch (IllegalArgumentException | AuthException e) {
            log.error("채팅방에 멤버 추가 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("채팅방에 멤버 추가 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("멤버 추가 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 채팅방 나가기 (현재 로그인한 사용자가 채팅방에서 나감)
     */
    public APIResponse<Void> leaveRoom(Long roomId) {
        try {
            log.info("채팅방 나가기 요청. roomId: {}", roomId);
            
            Member currentMember = securityUtil.getMember();
            log.debug("현재 사용자: {}", currentMember.getUsername());
            
            ChatRoom room = chatRoomRepository.findByIdWithMembers(roomId)
                    .orElseThrow(() -> {
                        log.error("채팅방을 찾을 수 없습니다. roomId: {}", roomId);
                        return new RuntimeException("채팅방을 찾을 수 없습니다.");
                    });

            // 현재 사용자가 채팅방에 속해있는지 확인
            boolean isMember = room.getMembers().stream()
                    .anyMatch(member -> member.getId().equals(currentMember.getId()));
            
            if (!isMember) {
                log.error("채팅방 멤버가 아닙니다. roomId: {}, memberId: {}", roomId, currentMember.getId());
                throw new AuthException(AuthErrorCode.FORBIDDEN);
            }

            // 멤버에서 제거
            room.getMembers().removeIf(member -> member.getId().equals(currentMember.getId()));
            
            // 마지막 멤버가 나가면 채팅방 삭제
            if (room.getMembers().isEmpty()) {
                log.info("마지막 멤버가 나갔으므로 채팅방 삭제. roomId: {}", roomId);
                chatRoomRepository.delete(room);
            } else {
                // 아직 멤버가 남아있으면 저장
                chatRoomRepository.save(room);
                log.info("채팅방에서 나가기 완료. roomId: {}, 남은 멤버 수: {}", 
                        roomId, room.getMembers().size());
            }

            return APIResponse.ok(null);
            
        } catch (AuthException e) {
            log.error("채팅방 나가기 실패: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("채팅방 나가기 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("채팅방 나가기 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("채팅방 나가기 중 오류가 발생했습니다.", e);
        }
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

