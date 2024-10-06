package com.ridetogether.server.global.oauth2.model.socialLoader;

import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.oauth2.model.SocialType;
import com.ridetogether.server.global.oauth2.model.info.KakaoOAuth2UserInfo;
import com.ridetogether.server.global.oauth2.model.info.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Slf4j
@Transactional
public class KakaoLoadsStrategy extends SocialLoadStrategy{

    protected OAuth2UserInfo setRequestToSocialSite(HttpEntity request) {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(SocialType.KAKAO.getUserInfoUrl(),
                    SocialType.KAKAO.getMethod(),
                    request,
                    RESPONSE_TYPE);
            return new KakaoOAuth2UserInfo(response.getBody()); // 카카오는 id를 PK로 사용
        } catch (Exception e) {
            log.error(ErrorStatus.KAKAO_SOCIAL_LOGIN_FAIL.getMessage(), e.getMessage());
            throw e;
        }
    }
}
