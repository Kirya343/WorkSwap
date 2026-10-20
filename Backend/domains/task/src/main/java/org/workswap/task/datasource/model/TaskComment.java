package org.workswap.task.datasource.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.workswap.user.datasource.model.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class TaskComment {

    public TaskComment(String content,
                       User author,
                       Task task
                       ) {
        this.content = content;
        this.author = author;
        this.task = task;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "author_id", insertable = false, updatable = false)
    private Long authorId;

    @Setter
    @ManyToOne
    private Task task;

    @Setter
    @CreationTimestamp
    private LocalDateTime createdAt;
}
