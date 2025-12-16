package technologyaa.Devit.domain.websocketchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.exception.AuthErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.websocketchat.dto.ChatMessage;
import technologyaa.Devit.domain.websocketchat.repository.ChatMessageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatUserService {
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    public APIResponse<List<ChatUserResponse>> getChattingUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Member currentMember = memberRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        // 채팅 메시지를 보낸 모든 고유한 유저 이름 조회
        List<String> distinctSenders = chatMessageRepository.findDistinctSenders();
        
        // 현재 유저를 제외하고 Member 엔티티로 변환
        List<ChatUserResponse> chattingUsers = distinctSenders.stream()
                .filter(sender -> !sender.equals(currentMember.getUsername()))
                .map(sender -> {
                    Member member = memberRepository.findByUsername(sender)
                            .orElse(null);
                    if (member != null) {
                        return ChatUserResponse.builder()
                                .id(member.getId())
                                .username(member.getUsername())
                                .email(member.getEmail())
                                .profile(member.getProfile())
                                .build();
                    }
                    return null;
                })
                .filter(user -> user != null)
                .collect(Collectors.toList());

        return APIResponse.ok(chattingUsers);
    }

    @lombok.Getter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ChatUserResponse {
        private Long id;
        private String username;
        private String email;
        private String profile;
    }
}

