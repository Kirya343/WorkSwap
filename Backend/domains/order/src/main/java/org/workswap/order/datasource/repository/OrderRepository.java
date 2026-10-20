package org.workswap.order.datasource.repository;

import org.springframework.stereotype.Repository;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.order.datasource.model.Order;
import org.workswap.user.datasource.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    
    Order findByBuyerAndSellerAndListing(User buyer, User seller, Listing listing);
    Order findByChatId(Long chatId);

    @Query("""
        SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
        FROM Order o
        WHERE o.id = :orderId
          AND (o.buyer.sub = :userSub OR o.seller.sub = :userSub)
    """)
    boolean existsByIdAndUserIsBuyerOrSeller(@Param("orderId") String orderId, @Param("userSub") String userSub);
}
