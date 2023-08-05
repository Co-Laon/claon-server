package claon.user.infra;

import claon.common.exception.ErrorCode;
import claon.common.exception.InternalServerErrorException;
import claon.user.dto.OAuth2UserInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InstagramUserInfoProvider implements OAuth2UserInfoProvider {
    @Value("${instagram.client-id}")
    private String clientId;

    @Value("${instagram.client-secret}")
    private String clientSecret;

    @Override
    public OAuth2UserInfoDto getUserInfo(String code) {
        ResponseEntity<String> response = getInstagramMeResponse(code);

        ObjectNode node = parseResponse(response);

        return OAuth2UserInfoDto.of(
                node.get("id").asText(),
                // TODO: Instagram permission required
                Optional.ofNullable(node.get("username"))
                        .map(JsonNode::asText)
                        .orElse(null)
        );
    }

    private String getAccessToken(String code) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add("client_id", this.clientId);
        parameters.add("client_secret", this.clientSecret);
        parameters.add("grant_type", "authorization_code");
        parameters.add("redirect_uri", "https://socialsizzle.herokuapp.com/auth/");
        parameters.add("code", code);

        ResponseEntity<String> response = new RestTemplate().exchange(
                "https://api.instagram.com/oauth/access_token",
                HttpMethod.POST,
                new HttpEntity<>(parameters, getAuthHeader()),
                String.class
        );

        ObjectNode node = parseResponse(response);

        return node.get("access_token").asText();
    }

    private ResponseEntity<String> getInstagramMeResponse(String code) {
        String accessToken = getAccessToken(code);

        return new RestTemplate().exchange(
                "https://graph.instagram.com/me?fields=id,name&access_token=" + accessToken,
                HttpMethod.GET,
                new HttpEntity<>(getAuthHeader()),
                String.class
        );
    }

    private ObjectNode parseResponse(ResponseEntity<String> response) {
        HttpStatus statusCode = response.getStatusCode();
        if (statusCode.isError()) {
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR, "인스타그램 로그인에 실패했습니다.");
        }

        try {
            return new ObjectMapper().readValue(response.getBody(), ObjectNode.class);
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR, "인스타그램 로그인에 실패했습니다.");
        }
    }

    private HttpHeaders getAuthHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded");

        return headers;
    }
}
