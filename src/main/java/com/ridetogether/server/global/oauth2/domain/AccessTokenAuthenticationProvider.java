package com.ridetogether.server.global.oauth2.domain;

import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.model.Role;
import com.ridetogether.server.global.oauth2.service.LoadMemberService;
import com.ridetogether.server.global.security.domain.CustomUserDetails;
import com.ridetogether.server.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {

    private final LoadMemberService loadMemberService;
    private final MemberRepository memberRepository;

    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // getOAuth2UserDetails에서 restTemplate과 AccessToken을 가지고 회원 정보 조회 -> 식별자 값 가져옴
        CustomUserDetails OAuth2User = loadMemberService.getOAuth2UserDetails((AccessTokenSocialTypeToken) authentication);

        Member member = saveOrGet(OAuth2User); // 받아온 식별자 값과 소셜로그인 방식을 통해 DB에서 회원 조회 / 없으면 새로 등록
        OAuth2User.updateAuthorities(member); // 역할 넣기

        // accessTokenSocialTypeToken 반환 -> 이렇게 하면 userDetails 타입으로 회원의 정보를 어디서든 조회 가능
        return AccessTokenSocialTypeToken.builder()
                .principal(OAuth2User)
                .authorities(OAuth2User.getAuthorities())
                .build();


    }

    private Member saveOrGet(CustomUserDetails oAuth2User) {
        // 소셜타입 필드에 넣기 !!!!! 코드 전체적으로 손보기
        return memberRepository.findBySocialTypeAndMemberId(oAuth2User.getSocialType(), oAuth2User.getMemberId())
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .socialType(oAuth2User.getSocialType())
                        .memberId(oAuth2User.getMemberId())
                        .role(Role.ROLE_GUEST)
                        .build()));
    }

    // 구현 고민하기 (Socia lType 때문)
    @Override
    public boolean supports(Class<?> authentication) {
        //AccessTokenSocialTypeToken타입의  authentication 객체이면 해당 Provider가 처리
        return AccessTokenSocialTypeToken.class.isAssignableFrom(authentication);
    }
}
