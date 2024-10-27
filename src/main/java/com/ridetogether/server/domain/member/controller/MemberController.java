package com.ridetogether.server.domain.member.controller;

import com.ridetogether.server.domain.image.application.OracleImageService;
import com.ridetogether.server.domain.image.dto.ImageDto.ImageUriResponseDto;
import com.ridetogether.server.domain.image.model.ImageType;
import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.member.dto.MemberDto.MemberSignupDto;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.CreateMemberRequestDto;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.UpdateMemberRequestDto;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.UpdatePasswordRequestDto;
import com.ridetogether.server.domain.member.dto.MemberResponseDto.IsDuplicatedDto;
import com.ridetogether.server.domain.member.dto.MemberResponseDto.MemberInfoResponseDto;
import com.ridetogether.server.domain.member.dto.MemberResponseDto.MemberTaskResultResponseDto;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import com.ridetogether.server.domain.member.converter.MemberDtoConverter;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.security.application.JwtService;
import com.ridetogether.server.global.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
//@RequestMapping("/api/member")
public class MemberController {

	private final MemberService memberService;
	private final JwtService jwtService;
	private final OracleImageService oracleImageService;

	@PostMapping("/api/member/signup")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<MemberTaskResultResponseDto> signUp(
			@Valid @RequestBody CreateMemberRequestDto requestDto) throws Exception {
		// request로 들어온 JSON 데이터를 회원가입 Dto로 변환
		MemberSignupDto memberSignupDto = MemberDtoConverter.convertRequestToSignupDto(requestDto);
		return ApiResponse.onSuccess(memberService.signUp(memberSignupDto));
	}

	@GetMapping("/api/member")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<MemberInfoResponseDto> getMyInfo() {
		return ApiResponse.onSuccess(memberService.getMyInfo());
	}

	@GetMapping("/api/member/all")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<List<MemberInfoResponseDto>> getAllMemberInfo() {
		return ApiResponse.onSuccess(memberService.getAllMemberInfo());
	}

	@PostMapping(value = "/api/member/image/{type}")
	public ApiResponse<ImageUriResponseDto> uploadImage(@RequestPart(value="image", required = true) MultipartFile image,
														@PathVariable("type") String type) throws Exception{
		ImageType imageType = ImageType.fromName(type);
		Member loginMember = SecurityUtil.getLoginMember()
				.orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

		Long imageIdx;
		if (imageType == ImageType.KAKAO) {
			imageIdx = oracleImageService.uploadKakaoQrImg(image, loginMember.getIdx());
		} else {
			imageIdx = oracleImageService.uploadProfileImg(image, loginMember.getIdx());
		}

		String accessUri = oracleImageService.getPublicImgUrl(imageIdx, loginMember.getIdx());

		memberService.checkExistPrevImageAndDeletePrev(imageIdx, loginMember.getIdx(), imageType);

		ImageUriResponseDto responseDto = ImageUriResponseDto.builder()
				.accessUri(accessUri)
				.build();
//		File convertFile = new File(System.getProperty("user.home") + "/rideTogetherDummy/" + image.getOriginalFilename());
//		oracleImageService.removeNewFile(convertFile);
		return ApiResponse.onSuccess(responseDto);

	}

	@GetMapping("/api/member/image/{type}")
	public ApiResponse<ImageUriResponseDto> getImage(@PathVariable("type") String type) throws Exception{
		Member loginMember = SecurityUtil.getLoginMember()
				.orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
		return ApiResponse.onSuccess(memberService.getImage(ImageType.fromName(type), loginMember.getIdx()));
	}

	@GetMapping("/api/member/isDuplicated")
	public ApiResponse<IsDuplicatedDto> isDuplicated(@RequestParam(value = "memberId", required = false) String memberId,
													 @RequestParam(value = "email", required = false) String email,
													 @RequestParam(value = "nickName", required = false) String nickName) throws Exception {
		IsDuplicatedDto isDuplicatedDto;
		if (memberId != null) {
			isDuplicatedDto = IsDuplicatedDto.builder()
					.isDuplicated(memberService.isExistByMemberId(memberId))
					.build();
		} else if (email != null) {
			isDuplicatedDto = IsDuplicatedDto.builder()
					.isDuplicated(memberService.isExistByEmail(email))
					.build();
		} else if (nickName != null) {
			isDuplicatedDto = IsDuplicatedDto.builder()
					.isDuplicated(memberService.isExistByNickName(nickName))
					.build();
		} else {
			throw new ErrorHandler(ErrorStatus._BAD_REQUEST);
		}
		return ApiResponse.onSuccess(isDuplicatedDto);

	}

	@PatchMapping("/api/member")
	public ApiResponse<MemberInfoResponseDto> updateMember(@Valid @RequestBody UpdateMemberRequestDto requestDto) {
		return ApiResponse.onSuccess(
				memberService.updateMember(MemberDtoConverter.convertRequestToUpdateDto(requestDto)));
	}

	@PatchMapping("/api/member/password")
	public ApiResponse<?> updatePassword(@Valid @RequestBody UpdatePasswordRequestDto requestDto) {
		return ApiResponse.onSuccess(memberService.updatePassword(requestDto));
	}

}
