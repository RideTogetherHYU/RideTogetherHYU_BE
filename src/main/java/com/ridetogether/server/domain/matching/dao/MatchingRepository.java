package com.ridetogether.server.domain.matching.dao;

import com.ridetogether.server.domain.matching.domain.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    Optional<Matching> findByIdx(Long idx);
}
