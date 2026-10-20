package org.workswap.task.datasource.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.workswap.task.datasource.model.Task;
import org.workswap.task.enums.TaskStatus;
import org.workswap.task.enums.TaskType;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
    Task findByName(String name);

    List<Task> findByExecutorId(Long executorId);

    @Query("""
        select distinct t
        from Task t
        left join User a on a.id = t.authorId
        left join User e on e.id = t.executorId
        where (:status is null or t.status = :status)
        and (:type is null or t.taskType = :type)
        order by
            case when :sort = 'created' then t.createdAt end desc,
            case when :sort = 'deadline' then t.deadline end asc,
            t.id asc
        """)
    Page<Task> findPageWithUsersFiltered(
        @Param("status") TaskStatus status,
        @Param("type") TaskType type,
        @Param("sort") String sort,
        Pageable pageable
    );

    List<Task> findByExecutorIdAndStatus(
        Long executorId,
        TaskStatus status
    );

    @Query("""
        select t
        from Task t
        where t.executorId = :userId
          and t.status = 'COMPLETED'
          and t.completed >= :from
        order by t.completed desc
    """)
    List<Task> findCompletedAfter(
        @Param("userId") Long userId,
        @Param("from") LocalDateTime from
    );

    @Query("""
        select count(t)
        from Task t
        where t.executorId = :userId
          and t.status = 'COMPLETED'
          and t.completed < :before
    """)
    long countCompletedBefore(
        @Param("userId") Long userId,
        @Param("before") LocalDateTime before
    );
}
