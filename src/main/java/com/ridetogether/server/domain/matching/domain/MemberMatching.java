package com.ridetogether.server.domain.matching.domain;

import com.ridetogether.server.domain.matching.model.ParticipantStatus;
import com.ridetogether.server.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberMatching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberMatching_idx")
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matching_idx")
    private Matching matching;

    @Enumerated(EnumType.STRING)
    private ParticipantStatus participantStatus;
}
