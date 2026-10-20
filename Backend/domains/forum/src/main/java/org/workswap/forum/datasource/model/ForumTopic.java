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
public class ForumTopic {

    public ForumTopic(User author, ForumTag tag, String title, String content, String language) {
        this.title = title;
        this.content = content;
        this.language = language;
        this.author = author;
        this.tag = tag;
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

    @Column(nullable = false)
    public String title;
    
    @Column(length = 2000, nullable = false)
    public String content;

    public String language;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    private ForumTag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Setter
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<ForumPost> posts = new ArrayList<>();

}
