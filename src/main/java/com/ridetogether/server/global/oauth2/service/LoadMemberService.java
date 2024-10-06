package com.ridetogether.server.global.oauth2.service;

import com.ridetogether.server.global.oauth2.domain.AccessTokenSocialTypeToken;
import com.ridetogether.server.global.oauth2.model.SocialType;
import com.ridetogether.server.global.oauth2.model.socialLoader.SocialLoadStrategy;
import com.ridetogether.server.global.security.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LoadMemberService { // 회원정보 받아오기
    private final RestTemplate restTemplate = new RestTemplate();


    public CustomUserDetails getOAuth2UserDetails(AccessTokenSocialTypeToken authentication) {
        SocialType socialType = authentication.getSocialType();
        SocialLoadStrategy socialLoadStrategy = getSocialLoadStrategy(socialType);
    }

    private SocialLoadStrategy getSocialLoadStrategy(SocialType socialType) {
        return switch (socialType) {

            case KAKAO -> new KakaoLoadStrategy();
            default -> throw new IllegalArgumentException("지원하지 않는 로그인 형식입니다");
        };
    }
}
