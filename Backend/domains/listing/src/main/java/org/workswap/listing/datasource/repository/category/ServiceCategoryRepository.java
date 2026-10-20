package org.workswap.listing.datasource.repository.category;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.workswap.category.datasource.CategoryRepository;
import org.workswap.listing.datasource.model.category.ServiceCategory;

@Repository
public interface ServiceCategoryRepository extends CategoryRepository<ServiceCategory> {

    @Query(value = """
        WITH RECURSIVE category_path AS (
            SELECT id, name, parent_id, leaf
            FROM service_category
            WHERE id = :categoryId

            UNION ALL

            SELECT c.id, c.name, c.parent_id, c.leaf
            FROM service_category c
            JOIN category_path cp ON c.id = cp.parent_id
        )
        SELECT * FROM category_path
        ORDER BY (parent_id IS NOT NULL), parent_id
        """, nativeQuery = true)
    List<ServiceCategory> findCategoryPathWithNativeQuery(@Param("categoryId") Long categoryId);
}
