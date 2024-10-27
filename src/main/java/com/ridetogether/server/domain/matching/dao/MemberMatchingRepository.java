package com.ridetogether.server.domain.matching.dao;

import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.domain.matching.domain.MemberMatching;
import com.ridetogether.server.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberMatchingRepository extends JpaRepository<MemberMatching, Long> {
    Optional<MemberMatching> findByIdx(Long idx);
    Optional<MemberMatching> findByMember(Member member);
    Optional<MemberMatching> findByMatching(Matching matching);
}
