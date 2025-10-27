package com.example.websocketchat.domain.member.oauth.dto;

import lombok.Builder;
import lombok.Getter;
import com.example.websocketchat.domain.member.oauth.entity.User;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    private final String picture;
    private final String provider;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture, String provider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.provider = provider != null ? provider : "google";
    }

    /**
     * 구글 OAuth 정보 매핑
     * @param registrationId OAuth 제공자 ID (예: "google")
     * @param userNameAttributeName 사용자 이름 속성 키
     * @param attributes OAuth 제공자로부터 받은 사용자 정보 맵
     * @return OAuthAttributes 객체
     */
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .provider(registrationId)
                .build();
    }

    /**
     * User 엔티티로 변환
     * @return User 엔티티 객체
     */
    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .provider(provider)
                .build();
    }
}

