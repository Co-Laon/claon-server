package claon.common.domain;

import lombok.Getter;

import javax.persistence.Id;

@Getter
public class RefreshToken {
    @Id
    private String token;
    private String userId;

    private RefreshToken(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    public static RefreshToken of(String token, String userId) {
        return new RefreshToken(token, userId);
    }
}
