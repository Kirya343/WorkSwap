package org.workswap.listing.datasource.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.dto.ShortListingDTO;

public interface ListingRepository extends JpaRepository<Listing, Long>, ListingRepositoryCustom {
    @Query("""
        SELECT DISTINCT l FROM Listing l
        LEFT JOIN FETCH l.translations t
        LEFT JOIN FETCH l.location loc
        LEFT JOIN FETCH l.serviceSettings ss
        LEFT JOIN FETCH ss.category ssc
        LEFT JOIN FETCH l.productSettings ps
        LEFT JOIN FETCH ps.category psc
        LEFT JOIN FETCH l.eventSettings es
        WHERE l.author.sub = :authorSub
    """)
    List<Listing> findByAuthorSubWithAllDetails(@Param("authorSub") String authorSub);

    List<Listing> findByAuthorId(Long userId);

    Listing findByAccessToken(String accessToken);

    List<Listing> findByAuthorIdAndActiveTrue(Long authorId);

    List<Listing> findByAuthorSubAndTemporary(String authorSub, boolean temporary);

    List<Listing> findByAuthorIdAndTemporaryAndActive(Long authorId, boolean temporary, boolean active);

    @EntityGraph(attributePaths = {
        "location",
        "author",
        "serviceSettings",
        "serviceSettings.category",
        "productSettings",
        "productSettings.category"
    })
    Page<Listing> findAllByTemporaryFalseOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = "select count(*) from favorite_listing where listing_id = :listingId", nativeQuery = true)
    int countFavoritesByListingId(@Param("listingId") Long listingId);
    int countByTemporary(boolean temporary);

    List<Listing> findByCreatedAtBefore(LocalDateTime date);

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM Listing l
        JOIN l.eventSettings es
        JOIN es.participants p
        WHERE l.id = :listingId AND p.sub = :userSub
    """)
    boolean existsParticipant(@Param("listingId") Long listingId, @Param("userSub") String userSub);
    boolean existsByIdAndAuthorSub(Long listingId, String authorSub);

    @Query("SELECT l.author.sub FROM Listing l WHERE l.id = :listingId")
    String findAuthorSubByListingId(@Param("listingId") Long listingId);   

    @Query("""
        SELECT new org.workswap.listing.dto.ShortListingDTO(
            l.id,

            COALESCE(tLang.title, MIN(tAny.title)),
            COALESCE(tLang.description, MIN(tAny.description)),

            l.price,
            l.priceType,
            l.type,

            CASE
                WHEN loc.city = false THEN loc.name
                ELSE CONCAT(country.name, ', ', loc.name)
            END,

            l.rating,
            l.imagePath,
            l.publishedAt,

            COUNT(DISTINCT u.id),

            CASE WHEN SUM(CASE WHEN u2.sub = :userSub THEN 1 ELSE 0 END) > 0
                THEN true ELSE false END
        )
        FROM Listing l

        LEFT JOIN l.favoredByUsers u
        LEFT JOIN l.favoredByUsers u2

        LEFT JOIN l.translations tLang
            ON tLang.language = :lang
        LEFT JOIN l.translations tAny

        LEFT JOIN l.location loc
        LEFT JOIN loc.country country

        WHERE l.id IN :listingIds

        GROUP BY
            l.id,
            tLang.title,
            tLang.description,
            l.price,
            l.priceType,
            l.type,
            loc.city,
            loc.name,
            country.name,
            l.rating,
            l.imagePath,
            l.publishedAt
    """)
    List<ShortListingDTO> findShortListingsByIds(
        @Param("listingIds") List<Long> listingIds,
        @Param("userSub") String userSub,
        @Param("lang") String lang
    );

    @Query("""
        SELECT new org.workswap.listing.dto.ShortListingDTO(
            l.id,

            COALESCE(tLang.title, MIN(tAny.title)),
            COALESCE(tLang.description, MIN(tAny.description)),

            l.price,
            l.priceType,
            l.type,

            CASE
                WHEN loc.city = false THEN loc.name
                ELSE CONCAT(country.name, ', ', loc.name)
            END,

            l.rating,
            l.imagePath,
            l.publishedAt,

            COUNT(DISTINCT uAll.id),

            true
        )
        FROM Listing l

        JOIN l.favoredByUsers uMe

        LEFT JOIN l.favoredByUsers uAll

        LEFT JOIN l.translations tLang
            ON tLang.language = :lang
        LEFT JOIN l.translations tAny

        LEFT JOIN l.location loc
        LEFT JOIN loc.country country

        WHERE uMe.sub = :userSub

        GROUP BY
            l.id,
            tLang.title,
            tLang.description,
            l.price,
            l.priceType,
            l.type,
            loc.city,
            loc.name,
            country.name,
            l.rating,
            l.imagePath,
            l.publishedAt
    """)
    List<ShortListingDTO> findLikedListings(
        @Param("userSub") String userSub,
        @Param("lang") String lang
    );

    @Query("""
        SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
        FROM Listing l JOIN l.favoredByUsers u
        WHERE l.id = :listingId AND u.sub = :userSub
        """)
    boolean existsFavoriteListing(
            @Param("userSub") String userSub,
            @Param("listingId") Long listingId
    );

    @Modifying
    @Transactional
    @Query(value = """
        INSERT IGNORE INTO favorite_listing(user_id, listing_id)
        SELECT u.id, :listingId
        FROM users u
        WHERE u.sub = :userSub
        """, nativeQuery = true)
    void addFavoriteListing(
        @Param("userSub") String userSub,
        @Param("listingId") Long listingId
    );


    @Modifying
    @Transactional
    @Query(value = """
        DELETE fl
        FROM favorite_listing fl
        JOIN users u ON u.id = fl.user_id
        WHERE u.sub = :userSub
        AND fl.listing_id = :listingId
        """, nativeQuery = true)
    void removeFavoriteListing(
        @Param("userSub") String userSub,
        @Param("listingId") Long listingId
    );

    @Modifying
    @Transactional
    @Query("""
        update Listing l
        set l.imagePath = :imagePath
        where l.id = :id
    """)
    void updateImagePath(@Param("id") Long id, @Param("imagePath") String imagePath);

    @Modifying
    @Transactional
    @Query("""
        update Listing l
        set l.imagePath = :imagePath
        where l.id = :id
        and (l.imagePath is null or l.imagePath = '')
    """)
    void setImagePathIfEmpty(@Param("id") Long id,
                            @Param("imagePath") String imagePath);

    @Query("""
        SELECT COALESCE(SUM(l.views), 0)
        FROM Listing l
        WHERE l.author.id = :userId
    """)
    long sumViewsByAuthorId(@Param("userId") Long userId);
}
