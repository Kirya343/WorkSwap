package org.workswap.task.datasource.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.workswap.task.enums.TaskStatus;
import org.workswap.task.enums.TaskType;
import org.workswap.user.datasource.model.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
public class Task {

    public Task(String name,
                String description,
                LocalDateTime deadline,
                TaskType taskType,
                User author) {
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.taskType = taskType;
        this.author = author;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 2000)
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime deadline;

    @Setter
    private LocalDateTime completed;

    @Setter
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.NEW;

    @Setter
    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Setter 
    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;

    @Column(name = "executor_id", insertable = false, updatable = false)
    private Long executorId;

    @Column(name = "author_id", insertable = false, updatable = false)
    private Long authorId;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskComment> comments = new ArrayList<>(); 

    @Transient // чтобы Hibernate не пытался сохранять это поле в БД
    public Duration getDuration() {
        if (deadline != null) {
            return Duration.between(LocalDateTime.now(), deadline);
        }
        return null;
    }
}
