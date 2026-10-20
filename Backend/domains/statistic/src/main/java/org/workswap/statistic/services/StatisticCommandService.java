package org.workswap.statistic.services;

import org.workswap.listing.datasource.model.Listing;
import org.workswap.shared.events.listing.ListingViewedEvent;
import org.workswap.statistic.dto.AllListingsStatSnapshotDTO;
import org.workswap.statistic.dto.OnlineStatSnapshotDTO;
import org.workswap.statistic.dto.SiteViewDTO;
import org.workswap.statistic.dto.UsersStatSnapshotDTO;
import org.workswap.statistic.enums.StatSaveIntervalType;
import org.workswap.user.datasource.model.User;

public interface StatisticCommandService {
    
    double calculateAverageRatingForUser(User user);
    double calculateAverageRatingForListing(Long listingId);

    void updateRatingForListing(Listing listing);
    void updateRatingForUser(User user);

    void cleanUpDuplicateListingsStat();
    void clearListingStatSnapshots(Long listingId);

    void saveListingsStat(StatSaveIntervalType intervalType);

    void saveListingView(ListingViewedEvent event);

    void saveOnlineStatSnapshot(OnlineStatSnapshotDTO dto);
    void saveUsersStatSnapshot(UsersStatSnapshotDTO dto);
    void saveListingsStatSnapshot(AllListingsStatSnapshotDTO dto);
    void saveSiteView(SiteViewDTO dto);
}
