package org.workswap.user.datasource.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class VerifyCode {

    public VerifyCode(String email) {
        this.code = new SecureRandom()
            .ints(6, 0, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".length())
            .mapToObj(i -> String.valueOf("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(i)))
            .collect(Collectors.joining());
        this.email = email;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    @Column(unique = true)
    private String email;

    @Setter
    private boolean verified = false;

    @CreationTimestamp
    private LocalDateTime timestamp;

    @Setter
    private LocalDateTime sentAt;
}
