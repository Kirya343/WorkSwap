package org.workswap.chat.datasource.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.workswap.user.datasource.model.User;

@Getter
@Entity
@NoArgsConstructor
public class Message {

    public Message(
        Chat chat,
        User sender,
        String text
    ) {
        this.chat = chat;
        this.sender = sender;
        this.text = text;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(name = "chat_id", insertable = false, updatable = false)
    private Long chatId;

    @Column(name = "sender_id", insertable = false, updatable = false)
    private Long senderId;

    @Column(columnDefinition = "TEXT")
    private String text;

    @CreationTimestamp
    private LocalDateTime sentAt;

    @Setter
    @Column(name = "is_read")
    private boolean read = false;
}
