package org.workswap.chat.datasource.model;

import org.workswap.user.datasource.model.User;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "chat_participants")
public class ChatParticipant {

    public ChatParticipant(Chat chat, User user) {
        this.chat = chat;
        this.user = user;
        this.id = new ChatParticipantId(chat.getId(), user.getId());
    }

    @EmbeddedId
    private ChatParticipantId id = new ChatParticipantId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatId")
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Setter
    private boolean chatTermsAccepted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
}