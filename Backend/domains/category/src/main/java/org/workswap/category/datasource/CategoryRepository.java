package org.workswap.category.datasource;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CategoryRepository<T extends Category> extends JpaRepository<T, Long> {

    List<T> findByParentIsNull();

    List<T> findByParentId(Long parentId);

    boolean existsByName(String name);

    List<T> findByLeaf(boolean leaf);

    T findByName(String categoryName);

    List<T> findByParent(T parent);
}
