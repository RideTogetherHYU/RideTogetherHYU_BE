package com.ridetogether.server.domain.member.dto;


import lombok.Builder;
import lombok.Data;

public class MemberRequestDto {
	@Builder
	@Data
	public static class CreateMemberRequestDto {
		private String memberId;
		private String password;
		private String name;
		private String email;
		private String nickName;
		private String gender;
		private String kakaoPayUrl;
		private String account;
		private String accountBank;
	}

	@Builder
	@Data
	public static class UpdateMemberRequestDto {
		private String memberId;
		private String name;
		private String email;
		private String nickName;
		private String gender;
		private String kakaoPayUrl;
		private String account;
		private String accountBank;
	}

	@Builder
	@Data
	public static class LoginMemberRequestDto {
		private String memberId;
		private String password;
	}

	@Builder
	@Data
	public static class UpdatePasswordRequestDto {
		private String memberId;
		private String password;
	}



}
