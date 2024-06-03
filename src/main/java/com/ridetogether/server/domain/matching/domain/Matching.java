package com.ridetogether.server.domain.matching.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ridetogether.server.domain.matching.model.MatchingStatus;
import com.ridetogether.server.domain.member.model.Gender;
import com.ridetogether.server.domain.member.model.PayType;
import com.ridetogether.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Matching extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_idx")
    private Long idx;

    private String title;

    private LocalDate ridingTime;

    private int participantCount;

    private String departure;

    private String destination;

    @Enumerated(EnumType.STRING)
    private MatchingStatus matchingStatus;

    private Gender matchingGender;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<PayType> payTypes;

    private LocalDate expiredAt;

    @OneToMany(mappedBy = "matching")
    @JsonIgnore
    private List<MemberMatching> memberMatching;




}
