package org.workswap.forum.datasource.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.workswap.shared.config.Constants;
import org.workswap.user.datasource.model.User;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class ForumComment {
    
    public ForumComment(ForumPost post, User author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String openId = NanoIdUtils.randomNanoId(
        NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
        Constants.ALPHANUMERIC,
        20
    );

    @Setter
    @Column(length = 3000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @CreationTimestamp
    private LocalDateTime createdAt; 

    @Setter
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private ForumPost post;
}
