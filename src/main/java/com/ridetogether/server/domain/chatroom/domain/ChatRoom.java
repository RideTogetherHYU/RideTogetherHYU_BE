package com.ridetogether.server.domain.chatroom.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ridetogether.server.domain.chat.domain.ChatMessage;
import com.ridetogether.server.domain.chat.model.ChatStatus;
import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 6494678977089006639L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private Long chatRoomId;

    private int userCount;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "matching_idx")
    private Matching matching;

    @Enumerated(EnumType.STRING)
    private ChatStatus chatStatus;

    public void addChatMessage(ChatMessage chatMessage) {
        chatMessages.add(chatMessage);
    }

    public ChatRoom inActive() {
        this.chatStatus = ChatStatus.INACTIVE;
        return this;
    }

    public void plusUserCount() {
        this.userCount++;
    }

    public void minusUserCount() {
        this.userCount--;
    }


}
