package org.workswap.chat.datasource.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.workswap.chat.enums.ChatStatus;
import org.workswap.chat.enums.ChatType;
import org.workswap.user.datasource.model.User;

@Getter
@Entity
@NoArgsConstructor
public class Chat {

    public Chat(
        Set<User> users,
        ChatType chatType,
        Long targetId
    ) {
        for (User user : users) {
            ChatParticipant participant = new ChatParticipant(this, user);
            participants.add(participant);
        }
        this.chatType = chatType;
        this.targetId = targetId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(
        mappedBy = "chat", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true, 
        fetch = FetchType.EAGER
    )
    private Set<ChatParticipant> participants = new HashSet<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    @Setter
    @Enumerated(EnumType.STRING)
    private ChatStatus status = ChatStatus.TEMPORARY;

    @Enumerated(EnumType.STRING)
    private ChatType chatType;

    private Long targetId;

    @Transient
    private long unreadCount;

    @Transient
    private String lastMessagePreview;

    @Transient
    private String lastMessageTime;

    @Transient
    private transient User interlocutor;
}

