package org.workswap.statistic.datasource.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UsersStatSnapshot {
    
    public UsersStatSnapshot(
        int standartsUsers,
        int tempUsers,
        LocalDateTime timestamp
    ) {
        this.usersCount = standartsUsers + tempUsers;
        this.standartsUsers = standartsUsers;
        this.tempUsers = tempUsers;
        this.timestamp = timestamp;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    private int usersCount;

    private int standartsUsers;
    private int tempUsers;

    private LocalDateTime timestamp;
}
