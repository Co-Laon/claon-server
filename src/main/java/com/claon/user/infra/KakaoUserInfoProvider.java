package com.claon.user.infra;

import com.claon.common.exception.ErrorCode;
import com.claon.common.exception.InternalServerErrorException;
import com.claon.user.dto.OAuth2UserInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoUserInfoProvider implements OAuth2UserInfoProvider {
    @Override
    public OAuth2UserInfoDto getUserInfo(String accessToken) {
        ResponseEntity<String> response = getKakaoMeResponse(accessToken);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode.isError()) {
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR, "카카오 로그인에 실패했습니다.");
        }

        ObjectNode node;
        try {
            node = new ObjectMapper().readValue(response.getBody(), ObjectNode.class);
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR, "카카오 로그인에 실패했습니다.");
        }

        return OAuth2UserInfoDto.of(
                node.get("id").asText(),
                node.get("kakao_account").get("email").asText()
        );
    }

    private ResponseEntity<String> getKakaoMeResponse(String accessToken) {
        HttpHeaders headers = this.getAuthHeader(accessToken);

        return new RestTemplate().exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
    }

    private HttpHeaders getAuthHeader(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return headers;
    }
}
