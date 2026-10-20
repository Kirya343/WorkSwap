package org.workswap.sso.datasource.model;

import java.time.LocalDateTime;

import org.workswap.sso.security.enums.UserStatus;

import jakarta.persistence.Entity;
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
public class UserDevice {

    public UserDevice (
        User user,
        String fingerprint,
        String userAgent,
        String ip
    ) {
        this.user = user;
        this.temp = user.getStatus().equals(UserStatus.TEMP);
        this.fingerprint = fingerprint;
        this.userAgent = userAgent;
        this.ip = ip;
        this.firstSeen = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String fingerprint;
    private String userAgent;
    private String ip;
    private boolean temp;

    private LocalDateTime firstSeen;

    @Setter
    private LocalDateTime lastSeen; 
}