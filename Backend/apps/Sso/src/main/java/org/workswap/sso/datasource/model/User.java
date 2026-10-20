package org.workswap.sso.datasource.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.workswap.sso.datasource.config.Constants;
import org.workswap.sso.security.enums.AuthProvider;
import org.workswap.sso.security.enums.UserStatus;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    public User(
        String name,
        String email,
        String avatarUrl,
        AuthProvider provider,
        boolean termsAccepted,
        String fingerprint,
        String userAgent,
        String ip
    ) {
        this.status = UserStatus.PENDING;
        this.email = email;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.termsAccepted = termsAccepted;
        if (termsAccepted) {
            this.termsAcceptanceDate = LocalDateTime.now();
        }
        this.provider = new HashSet<>(Set.of(AuthProvider.GOOGLE));
        this.devices.add(new UserDevice(this, fingerprint, userAgent, ip));
    }

    //локальная регистрация
    public User(
        String name,
        String email,
        String passwordHash,
        boolean termsAccepted,
        String fingerprint,
        String userAgent,
        String ip
    ) {
        this.status = UserStatus.PENDING;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.termsAccepted = termsAccepted;
        if (termsAccepted) {
            this.termsAcceptanceDate = LocalDateTime.now();
        }
        this.provider = new HashSet<>(Set.of(AuthProvider.LOCAL));
        this.devices.add(new UserDevice(this, fingerprint, userAgent, ip));
    }

    //временный пользователь
    public User(
        UserStatus status, 
        String fingerprint,
        String userAgent,
        String ip
    ) {
        this.status = status;
        this.provider = new HashSet<>(Set.of(AuthProvider.LOCAL));
        this.devices.add(new UserDevice(this, fingerprint, userAgent, ip));
    }

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String sub = NanoIdUtils.randomNanoId(
        NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
        Constants.ALPHANUMERIC,
        20
    );

    @Setter
    @Column(nullable = true, unique = true)
    private String name;

    @Column(nullable = true, unique = true)
    private String email;

    @Setter
    private String passwordHash;
    
    @Setter
    private String avatarUrl;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.PENDING;

    @Setter
    @ElementCollection(targetClass = AuthProvider.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "user_providers",
        joinColumns = @JoinColumn(name = "user_id"),
        foreignKey = @ForeignKey(name = "fk_user_providers")
    )
    @Column(name = "provider")
    private Set<AuthProvider> provider = new HashSet<>();

    @Setter
    private String phone;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Setter
    private boolean termsAccepted = false;

    @Setter
    private LocalDateTime termsAcceptanceDate = LocalDateTime.now();

    @Setter
    private LocalDateTime lastUsed;

    @OneToMany(
        mappedBy = "user", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true, 
        fetch = FetchType.LAZY
    )
    private List<UserDevice> devices = new ArrayList<>();

    @Setter
    @Transient
    private boolean isNew;
}
