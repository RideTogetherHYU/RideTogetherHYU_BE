package com.ridetogether.server.domain.member.application;

import static com.ridetogether.server.global.config.SecurityConfig.passwordEncoder;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.member;

import com.ridetogether.server.domain.image.application.ImageService;
import com.ridetogether.server.domain.image.domain.Image;
import com.ridetogether.server.domain.image.dto.ImageDto.ImageUriResponseDto;
import com.ridetogether.server.domain.image.model.ImageType;
import com.ridetogether.server.domain.member.converter.MemberDtoConverter;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.member.dto.MemberDto.MemberSignupDto;
import com.ridetogether.server.domain.member.dto.MemberDto.MemberUpdateDto;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.UpdatePasswordRequestDto;
import com.ridetogether.server.domain.member.dto.MemberResponseDto.MemberInfoResponseDto;
import com.ridetogether.server.domain.member.dto.MemberResponseDto.MemberTaskResultResponseDto;
import com.ridetogether.server.domain.member.model.ActiveState;
import com.ridetogether.server.domain.member.model.StudentStatus;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.security.application.JwtService;
import com.ridetogether.server.global.util.SecurityUtil;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final ImageService imageService;

	private static final String HANYANG_EMAIL = "@hanyang.ac.kr";

	public MemberTaskResultResponseDto signUp(MemberSignupDto memberSignupDto) throws Exception {
		if (isExistByEmail(memberSignupDto.getEmail())) {
			throw new ErrorHandler(ErrorStatus.MEMBER_EMAIL_ALREADY_EXIST);
		}
		if (isExistByNickName(memberSignupDto.getNickName())) {
			throw new ErrorHandler(ErrorStatus.MEMBER_NICKNAME_ALREADY_EXIST);
		}
		Member member = Member.builder()
				.memberId(memberSignupDto.getMemberId())
				.password(passwordEncoder().encode(memberSignupDto.getPassword()))
				.name(memberSignupDto.getName())
				.email(memberSignupDto.getEmail())
				.nickName(memberSignupDto.getNickName())
				.gender(memberSignupDto.getGender())
				.kakaoPayUrl(memberSignupDto.getKakaoPayUrl())
				.account(memberSignupDto.getAccount())
				.accountBank(memberSignupDto.getAccountBank())
				.role(memberSignupDto.getRole())
				.activeState(ActiveState.ACTIVE)
				.studentStatus(StudentStatus.NOT_STUDENT)
				.build();

		member.setStudentStatus(member.getMemberId());
		memberRepository.save(member);
		return  MemberTaskResultResponseDto.builder()
				.idx(member.getIdx())
				.nickName(member.getNickName())
				.isSuccess(true)
				.build();
	}

	public MemberInfoResponseDto getMyInfo() {
		Member member = SecurityUtil.getLoginMember().orElseThrow(() -> new ErrorHandler(ErrorStatus._UNAUTHORIZED));
		return MemberDtoConverter.convertMemberToInfoResponseDto(member);
	}

	public List<MemberInfoResponseDto> getAllMemberInfo() {
		List<MemberInfoResponseDto> dtoList = new ArrayList<>();
		List<Member> members = memberRepository.findAll();
		members.forEach(member -> dtoList.add(MemberDtoConverter.convertMemberToInfoResponseDto(member)));
		return dtoList;
	}

	public ImageUriResponseDto getImage(ImageType imageType, Long idx) {
		Member member = memberRepository.findByIdx(idx)
				.orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
		List<Image> images = member.getImages();
		if (images.isEmpty()) {
			throw new ErrorHandler(ErrorStatus.IMAGE_NOT_FOUND);
		}
		for (Image x : images) {
			if (x.getImageType() == imageType) {
				return ImageUriResponseDto.builder()
						.accessUri(x.getAccessUri())
						.build();
			}
		}
		return null;
	}

	public MemberInfoResponseDto updateMember(MemberUpdateDto updateDto) {
		Member member = memberRepository.findByMemberId(updateDto.getMemberId())
				.orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
		member.updateMember(updateDto);
		return MemberDtoConverter.convertMemberToInfoResponseDto(member);
	}

	public MemberTaskResultResponseDto updatePassword(UpdatePasswordRequestDto requestDto) {
		Member member = memberRepository.findByMemberId(requestDto.getMemberId())
				.orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
		member.updatePassword(passwordEncoder().encode(requestDto.getPassword()));
		return  MemberTaskResultResponseDto.builder()
				.idx(member.getIdx())
				.nickName(member.getNickName())
				.isSuccess(true)
				.build();
	}

	public void checkExistPrevImageAndDeletePrev(Long imageIdx, Long memberIdx, ImageType type) throws Exception{
		Member member = memberRepository.findByIdx(memberIdx)
				.orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
		List<Image> images = member.getImages();
		if (images.isEmpty()) {
			return;
		}
		for (Image x : images) {
			if (x.getImageType() == type && !x.getIdx().equals(imageIdx)) {
				imageService.deleteImg(x.getIdx());
				log.info("기존에 등록된 " + type + " 사진을 제거 완료하였습니다. ImageIdx = " + x.getIdx());
			}
		}

	}

	public void updateStudentStatusToStudent(Long memberIdx) {
		Member member = memberRepository.findByIdx(memberIdx)
				.orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
		member.updateStudentStatus(StudentStatus.STUDENT);
	}

	public boolean isExistByEmail(String email) {
		return memberRepository.existsByEmail(email);
	}

	public boolean isExistByNickName(String nickName) {
		return memberRepository.existsByNickName(nickName);
	}

	public boolean isExistByMemberId(String memberId) {
		return memberRepository.existsByMemberId(memberId);
	}

}
