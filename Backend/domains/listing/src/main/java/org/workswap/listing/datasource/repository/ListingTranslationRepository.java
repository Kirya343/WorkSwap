package org.workswap.listing.datasource.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.workswap.listing.datasource.model.ListingTranslation;

@Repository
public interface ListingTranslationRepository extends JpaRepository<ListingTranslation, Long> {

    @Query(value = """
        SELECT t.*
        FROM listing_translation t
        WHERE t.listing_id = :listingId
        ORDER BY
            CASE
                WHEN t.language = :lang THEN 1
                WHEN t.language = 'fi' THEN 2
                WHEN t.language = 'ru' THEN 3
                WHEN t.language = 'en' THEN 4
                ELSE 100
            END
        LIMIT 1
    """, nativeQuery = true)
    ListingTranslation findBestTranslation(
        @Param("listingId") Long listingId,
        @Param("lang") String lang
    );

    @Query(value = """
        SELECT *
        FROM (
            SELECT
                t.*,
                ROW_NUMBER() OVER (
                    PARTITION BY t.listing_id
                    ORDER BY
                        CASE
                            WHEN t.language = :lang THEN 1
                            WHEN t.language = 'fi' THEN 2
                            WHEN t.language = 'ru' THEN 3
                            WHEN t.language = 'en' THEN 4
                            ELSE 100
                        END
                ) AS rn
            FROM listing_translation t
            WHERE t.listing_id IN (:listingIds)
        ) ranked
        WHERE rn = 1
        """, nativeQuery = true)
    List<ListingTranslation> findBestTranslations(
        @Param("listingIds") List<Long> listingIds,
        @Param("lang") String lang
    );

    @Query("""
        SELECT t
        FROM ListingTranslation t
        WHERE t.id IN (
            SELECT MIN(t2.id)
            FROM ListingTranslation t2
            WHERE t2.listing.id IN :listingIds
            AND t2.language IN :languages
            GROUP BY t2.listing.id
        )
    """)
    List<ListingTranslation> findByListingIdsAndLanguages(
        @Param("listingIds") List<Long> listingIds,
        @Param("languages") List<String> languages
    );

    List<ListingTranslation> findByListingId(Long listingId);
}
