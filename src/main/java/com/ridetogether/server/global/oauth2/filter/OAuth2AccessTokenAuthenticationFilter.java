package com.ridetogether.server.global.oauth2.filter;

import com.ridetogether.server.global.oauth2.domain.AccessTokenAuthenticationProvider;
import com.ridetogether.server.global.oauth2.model.SocialType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Component
@Slf4j
public class OAuth2AccessTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_OAUTH2_LOGIN_REQUEST_URL = "/api/member/login/";  // /api/member/login/ 로 오는 요청 처리 (api명세서대로 일단 넣음)
    private static final String HTTP_METHOD = "GET"; // http method방식 : get
    private static final String ACCESS_TOKEN_HEADER_NAME = "Authorization"; // access토큰 헤더에 보낼 때, key = Authorization
    private static final AntPathRequestMatcher DEFAULT_OAUTH2_LOGIN_PATH_REQUEST_MATCHER
            = new AntPathRequestMatcher(DEFAULT_OAUTH2_LOGIN_REQUEST_URL +"*", HTTP_METHOD); //  /oauth2/login/* 의 요청에, GET으로 온 요청에 매칭

    public OAuth2AccessTokenAuthenticationFilter(AccessTokenAuthenticationProvider accessTokenAuthenticationProvider,   // provider
                                                 AuthenticationSuccessHandler authenticationSuccessHandler,  //로그인 성공 시 처리할  handler
                                                 AuthenticationFailureHandler authenticationFailureHandler) { //로그인 실패 시 처리할 handler

        super(DEFAULT_OAUTH2_LOGIN_PATH_REQUEST_MATCHER);   // 위에서 설정한  /oauth2/login/* 의 요청에, GET으로 온 요청을 처리하기 위해 설정

        this.setAuthenticationManager(new ProviderManager(accessTokenAuthenticationProvider));
        //AbstractAuthenticationProcessingFilter를 커스터마이징 하려면  ProviderManager를 꼭 지정해 주기 (안그러면 에러남)

        this.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        this.setAuthenticationFailureHandler(authenticationFailureHandler);

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        SocialType socialType = extractSocialType(request); // 어떤 소셜로그인 진행할지 타입추출

        String accessToken = request.getHeader(ACCESS_TOKEN_HEADER_NAME);
        log.info("{}", socialType.getSocialName());

        // AuthenticationManager에게 인증요청 보냄. Authentication 객체로는 AccessTokenSocialTypeToken을 커스텀해서 사용
        return this.getAuthenticationManager().authenticate(new AccessTokenSocialTypeToken(accessToken, socialType));
    }


    private SocialType extractSocialType(HttpServletRequest request) { // 소셜로그인 타입 추출해서 요청 처리
        return Arrays.stream(SocialType.values())
                .filter(socialType ->
                        socialType.getSocialName()
                                .equals(request.getRequestURI().substring(DEFAULT_OAUTH2_LOGIN_REQUEST_URL.length())))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 URL 주소입니다."));
    }
}
