package com.example.websocketchat.domain.profile.service;

import com.example.websocketchat.domain.member.entity.Member;
import com.example.websocketchat.domain.member.exception.AuthErrorCode;
import com.example.websocketchat.domain.member.repository.MemberRepository;
import com.example.websocketchat.domain.profile.dto.request.Updaterequest;
import com.example.websocketchat.domain.profile.dto.response.GetProfileRes;
import com.example.websocketchat.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final MemberRepository memberRepository;

    // 현재 로그인한 사용자를 가져오는 유틸 메서드
    private Member getCurrentMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new CustomException(AuthErrorCode.UNAUTHORIZED);
        }

        String username = auth.getName();
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public GetProfileRes getProfile() {
        Member member = getCurrentMember();
        return GetProfileRes.of(member);
    }

    @Transactional(readOnly = true)
    public GetProfileRes getProfileById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
        return GetProfileRes.of(member);
    }

    @Transactional
    public String updateProfile(Updaterequest request) {
        Member member = getCurrentMember();
        member.UpdateMember(request.username());
        memberRepository.save(member); // 🔹 명시적으로 저장해서 DB 반영
        return member.getUsername();
    }

    @Transactional
    public void deleteProfile() {
        Member member = getCurrentMember();
        memberRepository.delete(member);
    }
}