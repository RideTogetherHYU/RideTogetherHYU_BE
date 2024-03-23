package com.ridetogether.server.domain.member.dao;

import com.ridetogether.server.domain.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByMemberId(String memberId);
	Optional<Member> findByNickName(String memberId);
	Optional<Member> findByRefreshToken(String refreshToken);

	boolean existsByNickName(String nickName);
	boolean existsByEmail(String nickName);
}
