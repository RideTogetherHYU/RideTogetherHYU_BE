package com.ridetogether.server.global.util;

import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.security.domain.CustomUserDetails;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
	public static Optional<String> getLoginMemberId(){
		CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return Optional.ofNullable(user.getMemberId());
	}

}
