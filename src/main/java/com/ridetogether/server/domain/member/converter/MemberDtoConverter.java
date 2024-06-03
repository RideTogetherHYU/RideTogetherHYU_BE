package com.ridetogether.server.domain.member.converter;


import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.member.dto.MemberDto.MemberSignupDto;
import com.ridetogether.server.domain.member.dto.MemberDto.MemberUpdateDto;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.CreateMemberRequestDto;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.UpdateMemberRequestDto;
import com.ridetogether.server.domain.member.dto.MemberResponseDto.MemberInfoResponseDto;
import com.ridetogether.server.domain.member.model.Bank;
import com.ridetogether.server.domain.member.model.Gender;
import com.ridetogether.server.domain.member.model.Role;

public class MemberDtoConverter {

	public static MemberSignupDto convertRequestToSignupDto(CreateMemberRequestDto dto) {
		return MemberSignupDto.builder()
				.memberId(dto.getMemberId())
				.password(dto.getPassword())
				.name(dto.getName())
				.email(dto.getEmail())
				.nickName(dto.getNickName())
				.gender(Gender.fromName(dto.getGender()))
				.kakaoPayUrl(dto.getKakaoPayUrl())
				.account(dto.getAccount())
				.accountBank(Bank.fromName(dto.getAccountBank()))
				.role(Role.ROLE_ADMIN)
				.build();
	}

	public static MemberInfoResponseDto convertMemberToInfoResponseDto(Member member) {
		return MemberInfoResponseDto.builder()
				.idx(member.getIdx())
				.memberId(member.getMemberId())
				.name(member.getName())
				.email(member.getEmail())
				.nickName(member.getNickName())
				.gender(member.getGender().name())
				.kakaoPayUrl(member.getKakaoPayUrl())
				.account(member.getAccount())
				.accountBank(member.getAccountBank().getTitle())
				.studentStatus(member.getStudentStatus().toString())
				.build();
	}

	public static MemberUpdateDto convertRequestToUpdateDto(UpdateMemberRequestDto dto) {
		return MemberUpdateDto.builder()
				.memberId(dto.getMemberId())
				.name(dto.getName())
				.nickName(dto.getNickName())
				.gender(Gender.fromName(dto.getGender()))
				.kakaoPayUrl(dto.getKakaoPayUrl())
				.account(dto.getAccount())
				.accountBank(Bank.fromName(dto.getAccountBank()))
				.build();
	}

}
