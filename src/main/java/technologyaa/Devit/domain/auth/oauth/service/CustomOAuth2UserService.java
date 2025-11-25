package technologyaa.Devit.domain.auth.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import technologyaa.Devit.domain.auth.oauth.dto.OAuthAttributes;
import technologyaa.Devit.domain.auth.oauth.entity.User;
import technologyaa.Devit.domain.auth.oauth.repository.UserRepository;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    // 기본 사용자 권한 설정
    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(
                registrationId,
                userNameAttributeName,
                oAuth2User.getAttributes()
        );

        log.info("OAuth 로그인 시도 - Email: {}, Provider: {}, RegistrationId: {}", 
                attributes.getEmail(), attributes.getProvider(), registrationId);

        // DB에 사용자 저장 또는 업데이트
        User user = saveOrUpdate(attributes);

        log.info("OAuth 사용자 저장/업데이트 완료 - ID: {}, Email: {}", user.getId(), user.getEmail());

        // ROLE_USER 부여
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(DEFAULT_ROLE)),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    /**
     * 사용자 저장 또는 업데이트
     * @param attributes OAuth 속성
     * @return 저장/업데이트된 User 엔티티
     */
    private User saveOrUpdate(OAuthAttributes attributes) {
        return userRepository.findByEmailAndProvider(attributes.getEmail(), attributes.getProvider())
                .map(user -> {
                    log.debug("기존 사용자 정보 업데이트 - Email: {}", attributes.getEmail());
                    return user.update(attributes.getName(), attributes.getPicture());
                })
                .map(userRepository::save)
                .orElseGet(() -> {
                    log.debug("신규 사용자 저장 - Email: {}", attributes.getEmail());
                    User newUser = attributes.toEntity();
                    return userRepository.save(newUser);
                });
    }
}

