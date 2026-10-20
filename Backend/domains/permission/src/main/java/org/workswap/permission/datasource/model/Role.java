package org.workswap.permission.datasource.model;

import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class Role {

    public Role(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public Role(String name, Set<Permission> permissions, int level) {
        this.name = name;
        this.permissions = permissions;
        this.level = level;
    }

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true)
    private String name;

    @Setter
    @Column(nullable = false)
    private int level = 0;

    @Setter
    @ManyToMany
    private Set<Permission> permissions;

}
