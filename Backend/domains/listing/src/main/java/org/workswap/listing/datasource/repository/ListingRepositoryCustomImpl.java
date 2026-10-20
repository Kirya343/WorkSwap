package org.workswap.listing.datasource.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.workswap.category.datasource.Category;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.ListingTranslation;
import org.workswap.listing.datasource.model.category.ProductCategory;
import org.workswap.listing.datasource.model.category.ServiceCategory;
import org.workswap.listing.datasource.model.types.ProductSettings;
import org.workswap.listing.datasource.model.types.ServiceSettings;
import org.workswap.listing.dto.ShortListingDTO;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.enums.ProductType;
import org.workswap.listing.enums.ServiceType;
import org.workswap.location.datasource.model.Location;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.user.datasource.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.MapJoin;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Repository
public class ListingRepositoryCustomImpl implements ListingRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ShortListingDTO> findListings(
        List<? extends Category> categories,
        String locationName,
        String search,
        Boolean requireReviews,
        Boolean translationsFilter,
        List<String> languages,
        ListingType type,
        ServiceType serviceType,
        ProductType productType,
        String sortBy,
        Pageable pageable,
        Optional<UserAuthData> optAuthData
    ) {
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        // Основной запрос
        List<ShortListingDTO> content = Objects.requireNonNull(executeMainQuery(
            cb, categories, locationName, search, requireReviews,
            translationsFilter, languages, type, serviceType, productType,
            sortBy, pageable, optAuthData
        ));
        
        // Подсчет общего количества
        Long total = executeCountQuery(
            cb, categories, locationName, search, requireReviews,
            translationsFilter, languages, type, serviceType, productType
        );
        
        return new PageImpl<>(content, pageable, total);
    }

    private List<ShortListingDTO> executeMainQuery(
        CriteriaBuilder cb,
        List<? extends Category> categories,
        String locationName,
        String search,
        Boolean requireReviews,
        Boolean translationsFilter,
        List<String> languages,
        ListingType type,
        ServiceType serviceType,
        ProductType productType,
        String sortBy,
        Pageable pageable,
        Optional<UserAuthData> optAuthData
    ) {
        
        CriteriaQuery<ShortListingDTO> query = cb.createQuery(ShortListingDTO.class);
        Root<Listing> root = query.from(Listing.class);
        
        // Joins
        Join<Listing, Location> locationJoin = root.join("location", JoinType.LEFT);

        // Подзапрос для подсчета лайков [web:11][web:12]
        Subquery<Long> likesCountSubquery = buildLikesCountSubquery(cb, query, root);
        
        // Подзапрос для проверки "liked" [web:12]
        Expression<Boolean> likedExpr = optAuthData.isPresent() 
            ? buildLikedExpression(cb, query, root, optAuthData.get().sub())
            : cb.literal(false);

        // Projection
        query.select(cb.construct(
            ShortListingDTO.class,
            root.get("id"),
            cb.nullLiteral(String.class),
            cb.nullLiteral(String.class),
            root.get("price"),
            root.get("priceType"),  // Передаем enum напрямую
            root.get("type"),
            locationJoin.get("name"),
            root.get("rating"),
            root.get("imagePath"),
            root.get("publishedAt"),
            cb.coalesce(likesCountSubquery, 0L),
            likedExpr
        ));
        
        // Фильтрация
        List<Predicate> predicates = buildPredicates(
            cb, query, root,
            categories, locationName, search, requireReviews,
            translationsFilter, languages, type, serviceType, productType
        );
        
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        
        // Сортировка
        query.orderBy(buildOrderBy(cb, root, sortBy, likesCountSubquery));
        
        // Выполнение
        TypedQuery<ShortListingDTO> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        
        return typedQuery.getResultList();
    }

    private Long executeCountQuery(
        CriteriaBuilder cb,
        List<? extends Category> categories,
        String locationName,
        String search,
        Boolean requireReviews,
        Boolean translationsFilter,
        List<String> languages,
        ListingType type,
        ServiceType serviceType,
        ProductType productType
    ) {
        
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Listing> countRoot = countQuery.from(Listing.class);
        
        List<Predicate> predicates = buildPredicates(
            cb, countQuery, countRoot,
            categories, locationName, search, requireReviews,
            translationsFilter, languages, type, serviceType, productType
        );
        
        countQuery.select(cb.countDistinct(countRoot))
            .where(cb.and(predicates.toArray(new Predicate[0])));
        
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    /**
     * Подзапрос для подсчета количества лайков [web:11][web:12]
     */
    private Subquery<Long> buildLikesCountSubquery(
        CriteriaBuilder cb,
        CriteriaQuery<?> parentQuery,
        Root<Listing> parentRoot
    ) {
        
        Subquery<Long> subquery = parentQuery.subquery(Long.class);
        Root<Listing> subRoot = subquery.from(Listing.class);
        Join<Listing, User> favoritesJoin = subRoot.join("favoredByUsers", JoinType.LEFT);
        
        subquery.select(cb.count(favoritesJoin.get("id")))
            .where(cb.equal(subRoot.get("id"), parentRoot.get("id")));
        
        return subquery;
    }

    /**
     * Проверка, лайкнул ли пользователь объявление [web:12]
     */
    private Expression<Boolean> buildLikedExpression(
        CriteriaBuilder cb,
        CriteriaQuery<?> parentQuery,
        Root<Listing> parentRoot,
        String userSub
    ) {
        
        Subquery<Long> subquery = parentQuery.subquery(Long.class);
        Root<Listing> subRoot = subquery.from(Listing.class);
        Join<Listing, User> favoritesJoin = subRoot.join("favoredByUsers", JoinType.INNER);
        
        subquery.select(cb.literal(1L))
            .where(
                cb.equal(subRoot.get("id"), parentRoot.get("id")),
                cb.equal(favoritesJoin.get("sub"), userSub)
            );
        
        return cb.exists(subquery);
    }

    private List<Predicate> buildPredicates(
        CriteriaBuilder cb,
        CriteriaQuery<?> query,
        Root<Listing> root,
        List<? extends Category> categories,
        String locationName,
        String search,
        Boolean requireReviews,
        Boolean translationsFilter,
        List<String> languages,
        ListingType type,
        ServiceType serviceType,
        ProductType productType
    ) {
        
        List<Predicate> predicates = new ArrayList<>();
        
        // Базовые флаги
        predicates.add(cb.isTrue(root.get("active")));
        predicates.add(cb.isFalse(root.get("temporary")));
        predicates.add(cb.isFalse(root.get("testMode")));
        
        // Тип объявления
        applyTypeFilters(cb, root, predicates, type, serviceType, productType, categories);
        
        // Локация
        applyLocationFilter(cb, root, predicates, locationName);
        
        // Поиск по тексту
        applySearchFilter(cb, query, root, predicates, search);
        
        // Фильтр по языкам
        applyLanguageFilter(cb, query, root, predicates, translationsFilter, languages);
        
        // Отзывы
        if (Boolean.TRUE.equals(requireReviews)) {
            predicates.add(cb.gt(root.get("rating"), 0));
        }
        
        return predicates;
    }

    private void applyTypeFilters(
        CriteriaBuilder cb,
        Root<Listing> root,
        List<Predicate> predicates,
        ListingType type,
        ServiceType serviceType,
        ProductType productType,
        List<? extends Category> categories
    ) {
        
        if (type == null) {
            return;
        }
        
        predicates.add(cb.equal(root.get("type"), type));
        
        switch (type) {
            case SERVICE -> {
                Join<Listing, ServiceSettings> serviceJoin = 
                    root.join("serviceSettings", JoinType.INNER);
                
                if (serviceType != null) {
                    predicates.add(cb.equal(serviceJoin.get("type"), serviceType));
                }
                
                if (categories != null && !categories.isEmpty()) {
                    Join<ServiceSettings, ServiceCategory> categoryJoin = 
                        serviceJoin.join("category", JoinType.INNER);
                    predicates.add(categoryJoin.in(categories));
                }
            }
            
            case PRODUCT -> {
                Join<Listing, ProductSettings> productJoin = 
                    root.join("productSettings", JoinType.INNER);
                
                if (productType != null) {
                    predicates.add(cb.equal(productJoin.get("type"), productType));
                }
                
                if (categories != null && !categories.isEmpty()) {
                    Join<ProductSettings, ProductCategory> categoryJoin = 
                        productJoin.join("category", JoinType.INNER);
                    predicates.add(categoryJoin.in(categories));
                }
            }
            
            case EVENT -> {
                // EVENT не требует дополнительных фильтров
            }
        }
    }

    private void applyLocationFilter(
        CriteriaBuilder cb,
        Root<Listing> root,
        List<Predicate> predicates,
        String locationName
    ) {
        
        if (locationName == null || locationName.isBlank()) {
            return;
        }
        
        Join<Listing, Location> locationJoin = root.join("location", JoinType.INNER);
        Join<Location, Location> countryJoin = locationJoin.join("country", JoinType.LEFT);
        
        String normalizedName = locationName.toLowerCase();
        
        predicates.add(
            cb.or(
                cb.equal(cb.lower(locationJoin.get("name")), normalizedName),
                cb.equal(cb.lower(countryJoin.get("name")), normalizedName)
            )
        );
    }

    private void applySearchFilter(
        CriteriaBuilder cb,
        CriteriaQuery<?> query,
        Root<Listing> root,
        List<Predicate> predicates,
        String search
    ) {
        
        if (search == null || search.isBlank()) {
            return;
        }
        
        String pattern = "%" + search.toLowerCase() + "%";
        
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Listing> subRoot = subquery.from(Listing.class);
        MapJoin<Listing, String, ListingTranslation> translationsJoin = 
            subRoot.joinMap("translations", JoinType.INNER);
        
        subquery.select(cb.literal(1L))
            .where(
                cb.equal(subRoot.get("id"), root.get("id")),
                cb.or(
                    cb.like(cb.lower(translationsJoin.get("title")), pattern),
                    cb.like(cb.lower(translationsJoin.get("description")), pattern)
                )
            );
        
        predicates.add(cb.exists(subquery));
    }

    private void applyLanguageFilter(
        CriteriaBuilder cb,
        CriteriaQuery<?> query,
        Root<Listing> root,
        List<Predicate> predicates,
        Boolean translationsFilter,
        List<String> languages
    ) {
        
        if (!Boolean.TRUE.equals(translationsFilter) 
            || languages == null 
            || languages.isEmpty()) {
            return;
        }
        
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Listing> subRoot = subquery.from(Listing.class);
        MapJoin<Listing, String, ListingTranslation> translationsJoin = 
            subRoot.joinMap("translations", JoinType.INNER);
        
        subquery.select(cb.literal(1L))
            .where(
                cb.equal(subRoot.get("id"), root.get("id")),
                translationsJoin.key().in(languages),
                cb.isNotNull(translationsJoin.get("title")),
                cb.notEqual(translationsJoin.get("title"), ""),
                cb.isNotNull(translationsJoin.get("description")),
                cb.notEqual(translationsJoin.get("description"), "")
            );
        
        predicates.add(cb.exists(subquery));
    }

    private List<Order> buildOrderBy(
        CriteriaBuilder cb,
        Root<Listing> root,
        String sortBy,
        Subquery<Long> likesCountSubquery
    ) {
        
        if (sortBy == null) {
            return List.of(cb.desc(root.get("createdAt")));
        }
        
        return switch (sortBy) {
            case "price" -> List.of(cb.asc(root.get("price")));
            case "rating" -> List.of(cb.desc(root.get("rating")));
            case "date" -> List.of(cb.desc(root.get("publishedAt")));
            case "views" -> List.of(cb.desc(root.get("views")));
            case "likes" -> List.of(cb.desc(likesCountSubquery));
            default -> List.of(cb.desc(root.get("createdAt")));
        };
    }
}
