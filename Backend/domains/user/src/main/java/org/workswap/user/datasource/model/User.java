package org.workswap.user.datasource.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.workswap.location.datasource.model.Location;
import org.workswap.sso.security.enums.UserStatus;
import org.workswap.permission.datasource.model.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    // sso регистрация
    public User(
        String sub,
        String name,
        String email,
        String avatarUrl,
        Set<Role> roles,
        UserStatus status
    ) {
        this.sub = sub;
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.roles = roles;
        this.status = status;

        this.settings = new UserSettings(this);
    }

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String sub;

    @Setter
    @OneToOne(
        mappedBy = "user", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true, 
        fetch = FetchType.LAZY)
    private UserSettings settings;

    @Setter
    @Column(nullable = true, unique = true)
    private String name;

    @Column(nullable = true, unique = true)
    private String email;

    @Setter
    private String bio;

    @Setter
    private String avatarUrl;

    @Setter
    @ManyToOne
    private Location location;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.PENDING;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Role> roles;

    @Setter
    private Double rating = 0.0;

    @Setter
    private String phone;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Setter
    private boolean open = true;

    @Setter
    private LocalDateTime lastUsed;

    @Setter
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_languages", joinColumns = @JoinColumn(name = "user_id"))
    private List<String> languages = new ArrayList<>();

    @Setter
    @Transient
    private boolean isNew;
}
