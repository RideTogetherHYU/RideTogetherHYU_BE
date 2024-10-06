package com.ridetogether.server.global.oauth2.model;


import org.springframework.http.HttpMethod;

public enum SocialType { // 일단 카카오만 사용

    KAKAO(
            "kakao",
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.GET
    );

    private String socialName;
    private String userInfoUrl;
    private HttpMethod method;

    SocialType(String socialName, String userInfoUrl, HttpMethod method) {
        this.socialName = socialName;
        this.userInfoUrl = userInfoUrl;
        this.method = method;
    }

    public String getSocialName() {
        return socialName;
    }

    public String getUserInfoUrl() {
        return userInfoUrl;
    }

    public HttpMethod getMethod() {
        return method;
    }
}
