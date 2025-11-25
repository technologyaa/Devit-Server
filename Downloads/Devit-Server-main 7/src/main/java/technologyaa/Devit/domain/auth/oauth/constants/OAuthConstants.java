package technologyaa.Devit.domain.auth.oauth.constants;

/**
 * OAuth 관련 상수 정의
 * 다양한 OAuth 제공자와 권한 관리에 필요한 상수들을 정의합니다.
 */
public class OAuthConstants {
    
    private OAuthConstants() {
        throw new IllegalStateException("Utility class");
    }
    
    // OAuth 제공자
    public static final String PROVIDER_GOOGLE = "google";
    public static final String PROVIDER_NAVER = "naver";
    public static final String PROVIDER_KAKAO = "kakao";
    
    // 기본 권한
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    
    // OAuth 속성 키
    public static final String ATTRIBUTE_EMAIL = "email";
    public static final String ATTRIBUTE_NAME = "name";
    public static final String ATTRIBUTE_PICTURE = "picture";
    public static final String ATTRIBUTE_ID = "id";
}

