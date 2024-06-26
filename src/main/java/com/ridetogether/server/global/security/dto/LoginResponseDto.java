package com.ridetogether.server.global.security.dto;

import lombok.Builder;
import lombok.Data;

public class LoginResponseDto {

	@Data
	@Builder
	public static class LoginDto {
		public String memberId;
		public String accessToken;
		public String refreshToken;
	}

}
