package technologyaa.Devit.domain.websocketchat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.websocketchat.service.ChatRoomService;

import java.util.List;

@Tag(name = "채팅방 (Chat Room)", description = "채팅방 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/rooms")
@CrossOrigin(origins = {"http://localhost:5173", "https://devit.run"}, allowCredentials = "true")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성합니다.")
    @PostMapping
    public APIResponse<ChatRoomService.ChatRoomResponse> createRoom(
            @RequestBody ChatRoomService.ChatRoomRequest request) {
        return chatRoomService.createRoom(request);
    }

    @Operation(summary = "1:1 채팅방 조회/생성", description = "특정 사용자와의 1:1 채팅방을 조회하거나 생성합니다.")
    @GetMapping("/private/{memberId}")
    public APIResponse<ChatRoomService.ChatRoomResponse> getOrCreatePrivateRoom(
            @PathVariable Long memberId) {
        return chatRoomService.getOrCreatePrivateRoom(memberId);
    }

    @Operation(summary = "내 채팅방 목록", description = "내가 속한 채팅방 목록을 조회합니다.")
    @GetMapping("/my-rooms")
    public APIResponse<List<ChatRoomService.ChatRoomResponse>> getMyRooms() {
        return chatRoomService.getMyRooms();
    }

    @Operation(summary = "채팅방 상세 조회", description = "특정 채팅방의 상세 정보를 조회합니다.")
    @GetMapping("/{roomId}")
    public APIResponse<ChatRoomService.ChatRoomResponse> getRoom(@PathVariable Long roomId) {
        return chatRoomService.getRoom(roomId);
    }

    @Operation(summary = "채팅방에 멤버 추가", description = "그룹 채팅방에 멤버를 추가합니다.")
    @PostMapping("/{roomId}/members/{memberId}")
    public APIResponse<ChatRoomService.ChatRoomResponse> addMemberToRoom(
            @PathVariable Long roomId,
            @PathVariable Long memberId) {
        return chatRoomService.addMemberToRoom(roomId, memberId);
    }

    @Operation(summary = "채팅방 나가기", description = "현재 로그인한 사용자가 채팅방에서 나갑니다. 마지막 멤버가 나가면 채팅방이 삭제됩니다.")
    @DeleteMapping("/{roomId}/members/me")
    public APIResponse<Void> leaveRoom(@PathVariable Long roomId) {
        return chatRoomService.leaveRoom(roomId);
    }
}

