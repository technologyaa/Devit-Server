package technologyaa.Devit.domain.websocketchat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.websocketchat.dto.ChatMessage;
import technologyaa.Devit.domain.websocketchat.service.ChatMessageService;

import java.util.List;

@Tag(name = "채팅 메시지 (Chat Message)", description = "채팅 메시지 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/messages")
@CrossOrigin(origins = {"http://localhost:5173", "https://devit.run"}, allowCredentials = "true")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @Operation(summary = "채팅방 메시지 조회", description = "특정 채팅방의 메시지 목록을 조회합니다.")
    @GetMapping("/room/{roomId}")
    public APIResponse<List<ChatMessage>> getRoomMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return chatMessageService.getRoomMessages(roomId, page, size);
    }

    @Operation(summary = "1:1 채팅 메시지 조회", description = "특정 사용자와의 1:1 채팅 메시지를 조회합니다.")
    @GetMapping("/conversation/{username}")
    public APIResponse<List<ChatMessage>> getConversationMessages(@PathVariable String username) {
        return chatMessageService.getConversationMessages(username);
    }
}

