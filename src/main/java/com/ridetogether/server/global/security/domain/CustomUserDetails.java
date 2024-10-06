package com.ridetogether.server.global.security.domain;

import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.oauth2.model.SocialType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;

import java.util.*;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
	@Getter
	private SocialType socialType;
	private Collection<? extends GrantedAuthority> authorities;
	private final Member member;

	public CustomUserDetails(Member member) {
		this.member = member;
	}

	public Member getMember() {
		return member;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles = new ArrayList<>();

//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		return this.roles.stream()
//				.map(SimpleGrantedAuthority::new)
//				.collect(Collectors.toList());
//	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void updateAuthorities(Member member) {
		this.authorities = AuthorityUtils.createAuthorityList(member.getRole().toString());
	}
	@Override
	public String getPassword() {
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getMemberId();
	}

	public String getMemberId() {return member.getMemberId();}

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
