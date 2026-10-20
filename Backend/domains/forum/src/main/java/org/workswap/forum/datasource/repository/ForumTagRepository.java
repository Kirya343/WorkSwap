package org.workswap.forum.datasource.repository;

import org.springframework.stereotype.Repository;
import org.workswap.category.datasource.CategoryRepository;
import org.workswap.forum.datasource.model.ForumTag;

@Repository
public interface ForumTagRepository extends CategoryRepository<ForumTag> {
}
