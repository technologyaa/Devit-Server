package technologyaa.Devit.domain.websocketchat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.websocketchat.service.ChatUserService;

import java.util.List;

@Tag(name = "채팅 유저 (Chat User)", description = "채팅 유저 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/users")
public class ChatUserController {
    private final ChatUserService chatUserService;

    @Operation(summary = "채팅하고 있는 유저들 조회", description = "현재 유저와 채팅한 유저들의 목록을 조회합니다.")
    @GetMapping
    public APIResponse<List<ChatUserService.ChatUserResponse>> getChattingUsers() {
        return chatUserService.getChattingUsers();
    }
}

