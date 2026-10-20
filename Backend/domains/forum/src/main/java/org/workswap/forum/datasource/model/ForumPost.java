package org.workswap.forum.datasource.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.workswap.shared.config.Constants;
import org.workswap.user.datasource.model.User;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class ForumPost {

    public ForumPost(ForumTopic topic, User author, String content) {
        this.topic = topic;
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
    private ForumTopic topic;

    @Setter
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ForumComment> comments = new ArrayList<>();
}
