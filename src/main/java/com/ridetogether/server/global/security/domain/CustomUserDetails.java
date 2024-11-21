package com.ridetogether.server.global.security.domain;

import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.oauth2.model.SocialType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;

import java.util.*;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
public class CustomUserDetails implements UserDetails {


	@Getter
	private SocialType socialType;
	@Getter
	private String memberId;
	@Getter
	private String email;
	private String username;
	private Collection<? extends GrantedAuthority> authorities;

	@ElementCollection(fetch = FetchType.EAGER)
	@Builder.Default
	private List<String> roles = new ArrayList<>();

	public static CustomUserDetails create(Member member) {
		return CustomUserDetails.builder()
				.memberId(member.getMemberId())
				.email(member.getEmail())
				.socialType(member.getSocialType())
				.authorities(AuthorityUtils.createAuthorityList(member.getRole().toString()))
				.build();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void updateAuthorities(Member member) {
		this.authorities = AuthorityUtils.createAuthorityList(member.getRole().toString());
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return this.memberId;
	}


	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
