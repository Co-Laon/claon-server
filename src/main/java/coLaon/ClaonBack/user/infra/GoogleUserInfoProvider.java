package coLaon.ClaonBack.user.infra;

import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.InternalServerErrorException;
import coLaon.ClaonBack.user.dto.OAuth2UserInfoDto;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class GoogleUserInfoProvider implements OAuth2UserInfoProvider {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Override
    public OAuth2UserInfoDto getUserInfo(String code) {
        GoogleIdToken idToken = this.resolveIdToken(code);

        if (idToken == null) {
            throw new InternalServerErrorException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "구글 로그인에 실패했습니다."
            );
        }

        return OAuth2UserInfoDto.of(
                idToken.getPayload().getSubject(),
                idToken.getPayload().getEmail()
        );
    }

    private GoogleIdToken resolveIdToken(String code) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(this.clientId))
                .build();

        // Verify it
        try {
            return verifier.verify(code);
        } catch (GeneralSecurityException | IOException e) {
            throw new InternalServerErrorException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "구글 로그인에 실패했습니다."
            );
        }
    }
}
