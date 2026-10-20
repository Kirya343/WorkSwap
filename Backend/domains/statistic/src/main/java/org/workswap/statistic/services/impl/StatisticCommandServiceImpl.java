package org.workswap.statistic.services.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.statistic.dto.AllListingsStatSnapshotDTO;
import org.workswap.statistic.dto.OnlineStatSnapshotDTO;
import org.workswap.statistic.dto.SiteViewDTO;
import org.workswap.statistic.dto.UsersStatSnapshotDTO;
import org.workswap.statistic.enums.StatSaveIntervalType;
import org.workswap.statistic.services.StatisticCommandService;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.review.datasource.model.Review;
import org.workswap.review.services.ReviewQueryService;
import org.workswap.shared.events.listing.ListingViewedEvent;
import org.workswap.statistic.datasource.model.AllListingsStatSnapshot;
import org.workswap.statistic.datasource.model.ListingStatSnapshot;
import org.workswap.statistic.datasource.model.ListingView;
import org.workswap.statistic.datasource.model.OnlineStatSnapshot;
import org.workswap.statistic.datasource.model.SiteView;
import org.workswap.statistic.datasource.model.UsersStatSnapshot;
import org.workswap.statistic.datasource.repository.AllListingsStatRepository;
import org.workswap.statistic.datasource.repository.ListingStatRepository;
import org.workswap.statistic.datasource.repository.ListingViewRepository;
import org.workswap.statistic.datasource.repository.OnlineStatRepository;
import org.workswap.statistic.datasource.repository.SiteViewRepository;
import org.workswap.statistic.datasource.repository.UsersStatRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Profile({"server", "statistic"})
@Slf4j
@RequiredArgsConstructor
public class StatisticCommandServiceImpl implements StatisticCommandService {
    
    private final ListingRepository listingRepository;
    private final ListingStatRepository listingStatRepository;
    private final ListingViewRepository listingViewRepository;
    private final OnlineStatRepository onlineStatRepository;
    private final UsersStatRepository usersStatRepository;
    private final SiteViewRepository siteViewRepository;
    private final AllListingsStatRepository allListingsStatRepository;

    private final ReviewQueryService reviewQueryService;
    private final UserRepository userRepository;

    @Transactional
    public void cleanUpDuplicateListingsStat() {
        List<ListingStatSnapshot> allSnapshots = listingStatRepository.findAll(Sort.by("listingId", "intervalType", "time"));

        log.debug("Найдено снапшотов: {}", allSnapshots.size());

        Map<String, ListingStatSnapshot> seenSnapshots = new HashMap<>();
        List<ListingStatSnapshot> toDelete = new ArrayList<>();

        for (ListingStatSnapshot snapshot : allSnapshots) {
            String key = snapshot.getListingId() + "-" +
                         snapshot.getViews() + "-" +
                         snapshot.getFavorites() + "-" +
                         snapshot.getRating() + "-" +
                         snapshot.getIntervalType();


            if (seenSnapshots.containsKey(key)) {
                toDelete.add(snapshot); // это повтор — добавляем в список на удаление
            } else {
                seenSnapshots.put(key, snapshot); // первый встретившийся снапшот
            }
        }

        listingStatRepository.deleteAll(toDelete);
        log.debug("Удалено {} дубликатов статистики.", toDelete.size());
    }

    public void clearListingStatSnapshots(Long listingId) {
        listingStatRepository.deleteAllByListingId(listingId);
    }

    @Transactional
    public void saveListingsStat(StatSaveIntervalType intervalType) {
        Duration checkWindow;

        // Устанавливаем окно времени для каждого интервала
        switch (intervalType) {
            case FIVE_MINUTES -> checkWindow = Duration.ofMinutes(4);
            case HOURLY -> checkWindow = Duration.ofMinutes(50);
            case DAILY -> checkWindow = Duration.ofHours(20);
            case WEEKLY -> checkWindow = Duration.ofDays(6); // для недельного (можно менять)
            default -> throw new IllegalArgumentException("Unknown interval: " + intervalType);
        }

        LocalDateTime since = LocalDateTime.now().minus(checkWindow);
        List<Listing> listings = listingRepository.findAll();

        for (Listing listing : listings) {
            long count = listingStatRepository.countRecentSnapshots(listing.getId(), intervalType, since);
            if (count > 0) {
                continue; // уже есть снапшот за этот период
            }

            ListingStatSnapshot stat = new ListingStatSnapshot(
                listing.getId(),
                listing.getViews(),
                listingRepository.countFavoritesByListingId(listing.getId()),
                listing.getRating(),
                intervalType
            );

            listingStatRepository.save(stat);
        }
    }

    public double calculateAverageRatingForListing(Long listingId) {
        List<Review> reviews = reviewQueryService.getReviewsByListingId(listingId);

        if (reviews.isEmpty()) {
            return 0;  // Если нет отзывов, возвращаем 0
        }

        double totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }

        return totalRating / reviews.size();  // Средний рейтинг
    }

    public double calculateAverageRatingForUser(User user) {
        List<Listing> listings = listingRepository.findByAuthorId(user.getId());  // Получаем все объявления пользователя

        List<Review> listingReviews = listings.stream()
            .flatMap(listing -> reviewQueryService.getReviewsByListingId(listing.getId()).stream())
            .toList();

        List<Review> profileReviews = reviewQueryService.getReviewsByProfileSub(user.getSub());

        List<Review> allReviews = new ArrayList<>();
        allReviews.addAll(listingReviews);
        allReviews.addAll(profileReviews);

        if (listings.isEmpty()) {
            return 0;  // Если у пользователя нет объявлений, возвращаем 0
        }

        double totalRating = 0;
        int totalReviews = 0;

        for (Review review : allReviews) {
            double rating = review.getRating();  // Получаем рейтинг для каждого объявления

            totalRating += rating;
            totalReviews++;
        }

        // Если есть хотя бы одно объявление с рейтингом больше 1, считаем средний рейтинг
        if (totalReviews > 0) {
        double average = totalRating / totalReviews;
        return Math.round(average * 10.0) / 10.0;  // округление до 1 знака после запятой
        } else {
            return 0.0;
        }
    }

    public void updateRatingForListing(Listing listing) {
        double newListingRating = calculateAverageRatingForListing(listing.getId());
        listing.setRating(newListingRating);
        listingRepository.save(listing);
        
        updateRatingForUser(listing.getAuthor());
    }

    public void updateRatingForUser(User user) {
        double newUserRating = calculateAverageRatingForUser(user);
        user.setRating(newUserRating);
        userRepository.save(user);
    }

    public void saveListingView(ListingViewedEvent event) {
        boolean alreadyExists = true;
        if (event.userSub() != null && event.listingId() != null) {
            log.debug("Айди объявления: {}", event.listingId());
            log.debug("Айди пользователя: {}", event.userSub());

            User user = userRepository.findBySub(event.userSub()).orElseThrow();

            alreadyExists = listingViewRepository.existsByUserIdAndListingId(user.getId(), event.listingId());

            log.debug("Просмотр уже существует? {}", alreadyExists);

            if (alreadyExists == false) {
                ListingView newView = new ListingView(
                    user.getId(), 
                    event.listingId(), 
                    event.temporary(),
                    event.timestamp()
                );
                listingViewRepository.save(newView);

                Listing listing = listingRepository.findById(event.listingId()).orElseThrow(
                    () -> new EntityNotFoundException("Объявление не найдено"));

                int views = listing.getViews();
                listing.setViews(views + 1);
                listingRepository.save(listing);
            }
        }
    }

    public void saveOnlineStatSnapshot(OnlineStatSnapshotDTO dto) {
        OnlineStatSnapshot snapshot = new OnlineStatSnapshot(
            dto.online(), 
            dto.timestamp());
        onlineStatRepository.save(snapshot);
    }

    public void saveUsersStatSnapshot(UsersStatSnapshotDTO dto) {
        UsersStatSnapshot snapshot = new UsersStatSnapshot(
            dto.standartsUsers(),
            dto.tempUsers(),
            dto.timestamp());
        usersStatRepository.save(snapshot);
    }

    public void saveListingsStatSnapshot(AllListingsStatSnapshotDTO dto) {
        AllListingsStatSnapshot snapshot = new AllListingsStatSnapshot(
            dto.publichedListings(),
            dto.tempListings(),
            dto.timestamp());
        allListingsStatRepository.save(snapshot);
    }

    public void saveSiteView(SiteViewDTO dto) {
        SiteView snapshot = new SiteView(
            dto.codeName(),
            dto.timestamp());
        siteViewRepository.save(snapshot);
    }
}
