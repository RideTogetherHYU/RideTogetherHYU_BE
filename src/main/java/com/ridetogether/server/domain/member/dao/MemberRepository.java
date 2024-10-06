package com.ridetogether.server.domain.member.dao;

import com.ridetogether.server.domain.member.domain.Member;
import java.util.Optional;

import com.ridetogether.server.global.oauth2.model.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByIdx(Long idx);
	Optional<Member> findByMemberId(String memberId);
	Optional<Member> findByEmail(String email);
	Optional<Member> findByNickName(String memberId);
	Optional<Member> findByRefreshToken(String refreshToken);
	Optional<Member> findBySocialTypeAndMemberId(SocialType socialType, String memberId);
	boolean existsByNickName(String nickName);
	boolean existsByEmail(String nickName);

	boolean existsByMemberId(String memberId);


}
