package com.ridetogether.server.domain.matching.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.matching.dto.MatchingDto;
import com.ridetogether.server.domain.matching.model.MatchingStatus;
import com.ridetogether.server.domain.member.model.Gender;
import com.ridetogether.server.domain.member.model.PayType;
import com.ridetogether.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.ridetogether.server.domain.matching.dto.MatchingDto.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Matching extends BaseTimeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 6494678977089006639L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_idx")
    private Long idx;

    private Long hostMemberIdx;

    private String hostMemberNickName;

    private String title;

    private String ridingTime;

    private int participantCount;

    private int maxParticipantCount;

    private String departure;

    private String destination;

    @Enumerated(EnumType.STRING)
    private MatchingStatus matchingStatus;

    private Gender matchingGender;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<PayType> payTypes;

    @OneToOne
    @JoinColumn(name = "chatRoom_idx")
    private ChatRoom chatRoom;

    private LocalDate expiredAt;

    private int price;

    private Long realTimeMatchId;

    @OneToMany(mappedBy = "matching")
    @JsonIgnore
    @Builder.Default
    private List<MemberMatching> memberMatching = new ArrayList<>();


    public void addMemberMatching(MemberMatching memberMatching) {
        this.memberMatching.add(memberMatching);
    }

    public void updateMatchingStatus(MatchingStatus matchingStatus) {
        this.matchingStatus = matchingStatus;
    }
    public void updatePrice(int price) {
        this.price = price;
    }

    public void updateRealTimeMatchId(Long realTimeMatchId) {
        this.realTimeMatchId = realTimeMatchId;
    }

    public void updateMatching(UpdateMatchingDto dto) {
        this.title = dto.getTitle();
        this.ridingTime = dto.getRidingTime();
        this.participantCount = dto.getParticipantCount();
        this.maxParticipantCount = dto.getMaxParticipantCount();
        this.departure = dto.getDeparture();
        this.destination = dto.getDestination();
        this.matchingStatus = dto.getMatchingStatus();
    }

}
