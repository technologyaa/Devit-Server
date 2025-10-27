package technologyaa.devit.domain.member.oatuh.dto;

import lombok.Getter;
import technologyaa.devit.domain.member.oatuh.entity.User;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    // 구글 OAuth 정보 매핑
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return new OAuthAttributes(
                attributes,
                userNameAttributeName,
                (String) attributes.get("name"),
                (String) attributes.get("email"),
                (String) attributes.get("picture")
        );
    }

    public User toEntity() {
        return new User(name, email, picture, "google");
    }

      // ✅ Getter
//    public Map<String, Object> getAttributes() { return attributes; }
//    public String getNameAttributeKey() { return nameAttributeKey; }
//    public String getName() { return name; }
//    public String getEmail() { return email; }
//    public String getPicture() { return picture; }
}
