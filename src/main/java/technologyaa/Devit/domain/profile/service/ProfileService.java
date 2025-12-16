package technologyaa.Devit.domain.profile.service;

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
import technologyaa.Devit.domain.profile.dto.request.UpdateRequest;
import technologyaa.Devit.domain.profile.dto.response.GetResponse;
import technologyaa.Devit.domain.profile.dto.response.ProfileResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final MemberRepository memberRepository;

    /** 현재 로그인 사용자 조회 */
    private Member getCurrentMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        return memberRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));
    }

    /** 내 프로필 조회 */
    @Transactional(readOnly = true)
    public APIResponse<ProfileResponse> getProfile() {
        Member member = getCurrentMember();
        return APIResponse.ok(ProfileResponse.of(member));
    }

    /** ID로 프로필 조회 */
    @Transactional(readOnly = true)
    public APIResponse<GetResponse> getProfileById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        return APIResponse.ok(GetResponse.of(member));
    }

    /** 프로필 수정 */
    public APIResponse<ProfileResponse> updateProfile(UpdateRequest request) {
        Member member = getCurrentMember();

        if (request.username() != null) {
            member.setUsername(request.username());
        }
        if (request.profile() != null) {
            member.setProfile(request.profile());
        }

        return APIResponse.ok(ProfileResponse.of(member));
    }

    /** 프로필 삭제 */
    public APIResponse<Void> deleteProfile() {
        Member member = getCurrentMember();
        memberRepository.delete(member);
        return APIResponse.ok(null);
    }

    /** 기본 프로필 조회 (비로그인) */
    @Transactional(readOnly = true)
    public APIResponse<ProfileResponse> getDefaultProfile() {
        Member member = memberRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        return APIResponse.ok(ProfileResponse.of(member));
    }
}