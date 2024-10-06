package com.ridetogether.server.global.oauth2.domain;

import com.ridetogether.server.global.oauth2.model.SocialType;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import lombok.Builder;

import java.util.Collection;

public class AccessTokenSocialTypeToken extends AbstractAuthenticationToken {

    private Object principal; // OAuth2UserDetails 타입
    @Getter
    private String accessToken;
    @Getter
    private SocialType socialType;

    public AccessTokenSocialTypeToken(String accessToken, SocialType socialType) {
        super(null);
        this.accessToken = accessToken;
        this.socialType = socialType;
        setAuthenticated(false);
    }

    @Builder
    public AccessTokenSocialTypeToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true); // must use super, as we override
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
