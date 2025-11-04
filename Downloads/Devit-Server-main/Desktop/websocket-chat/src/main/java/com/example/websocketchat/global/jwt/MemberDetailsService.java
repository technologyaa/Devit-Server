package com.example.websocketchat.global.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.websocketchat.domain.member.entity.Member;
import com.example.websocketchat.domain.member.entity.Role;
import com.example.websocketchat.domain.member.repository.MemberRepository;
import com.example.websocketchat.domain.member.oauth.entity.User;
import com.example.websocketchat.domain.member.oauth.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 먼저 Member 테이블에서 찾기
        Member member = memberRepository.findByUsername(username).orElse(null);
        if (member != null) {
            return new MemberDetails(member);
        }
        
        // 없으면 이메일로 User 테이블에서 찾기 (OAuth 사용자)
        User oauthUser = userRepository.findByEmail(username).orElse(null);
        if (oauthUser != null) {
            // OAuth 사용자를 위한 UserDetails 생성
            return new OAuthUserDetails(oauthUser);
        }
        
        throw new UsernameNotFoundException("회원을 찾을 수 없습니다: " + username);
    }
    
    /**
     * OAuth 사용자를 위한 UserDetails 구현
     */
    private static class OAuthUserDetails implements UserDetails {
        private final User user;
        
        public OAuthUserDetails(User user) {
            this.user = user;
        }
        
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority(Role.ROLE_USER.name()));
        }
        
        @Override
        public String getPassword() {
            return ""; // OAuth 사용자는 비밀번호가 없음
        }
        
        @Override
        public String getUsername() {
            return user.getEmail(); // 이메일을 username으로 사용
        }
        
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }
        
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }
        
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }
        
        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}

