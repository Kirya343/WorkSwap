package org.workswap.forum.datasource.model;

import java.util.ArrayList;
import java.util.List;

import org.workswap.category.datasource.Category;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ForumTag extends Category {
    
    @ManyToOne(fetch = FetchType.LAZY)
    private ForumTag parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ForumTag> children = new ArrayList<>();
    
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    private List<ForumTopic> topics = new ArrayList<>();

    @Override
    public ForumTag getParent() { return parent; }

    @Override
    public List<ForumTag> getChildren() { return children; }

    public ForumTag(String name, ForumTag parent) {
        this.name = name;
        this.parent = parent;
    }
}
