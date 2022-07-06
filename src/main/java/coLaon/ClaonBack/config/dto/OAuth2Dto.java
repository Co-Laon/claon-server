package coLaon.ClaonBack.config.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class OAuth2Dto {
    private final Map<String, Object> attributes;
    private final String attributeKey;
    private final String email;

    public OAuth2Dto(
            Map<String, Object> attributes,
            String attributeKey,
            String email
    ) {
        this.attributes = attributes;
        this.attributeKey = attributeKey;
        this.email = email;
    }

    public static OAuth2Dto of(
            String provider,
            String userNameAttributeName,
            Map<String, Object> attributes
    ) {
        switch (provider) {
            case "google":
                return ofGoogle(userNameAttributeName, attributes);
            case "kakao":
                return ofKakao(attributes);
            default:
                throw new RuntimeException();
        }
    }

    private static OAuth2Dto ofGoogle(
            String userNameAttributeName,
            Map<String, Object> attributes
    ) {
        return new OAuth2Dto(
                attributes,
                userNameAttributeName,
                (String) attributes.get("email")
        );
    }

    private static OAuth2Dto ofKakao(
            Map<String, Object> attributes
    ) {
        return new OAuth2Dto(
                (Map<String, Object>) attributes.get("kakao_account"),
                "email",
                (String) attributes.get("email")
        );
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("email", this.email);

        return map;
    }
}
