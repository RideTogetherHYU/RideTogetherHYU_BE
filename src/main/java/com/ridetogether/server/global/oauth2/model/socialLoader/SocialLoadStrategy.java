package com.ridetogether.server.global.oauth2.model.socialLoader;

import com.ridetogether.server.global.oauth2.model.info.OAuth2UserInfo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.Collections;
import java.util.Map;

public abstract class SocialLoadStrategy {
    ParameterizedTypeReference<Map<String, Object>> RESPONSE_TYPE = new ParameterizedTypeReference<>(){};
    protected final RestTemplate restTemplate = new RestTemplate();

    public OAuth2UserInfo getSocialInfo(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        setHeaders(accessToken, httpHeaders);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, httpHeaders);

        return setRequestToSocialSite(request); // 구체 클래스가 구현
    }

    protected abstract OAuth2UserInfo setRequestToSocialSite(HttpEntity request);

    public void setHeaders(String accessToken, HttpHeaders headers) {
        headers.set("Authorization", accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }
}
