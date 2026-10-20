package org.workswap.order.dto;

import java.time.LocalDateTime;

import org.workswap.chat.datasource.model.Chat;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.order.datasource.model.Order;
import org.workswap.user.datasource.model.User;

public record OrderDTO(
    String id,
    Long listingId,
    Long buyerId,
    Long sellerId,
    Long chatId,

    String status,
    boolean confirmedByBuyer,
    boolean confirmedBySeller,

    LocalDateTime createdAt
) {
    public static OrderDTO ofOrder(Order order) {

        Listing listing = order.getListing();
        User buyer = order.getBuyer();
        User seller = order.getSeller();
        Chat chat =  order.getChat();

        return new OrderDTO(
            order.getId(),
            listing != null ? listing.getId() : null,
            buyer != null ? buyer.getId() : null,
            seller != null ? seller.getId() : null,
            chat != null ? chat.getId() : null,
            order.getStatus().toString(),
            order.isConfirmedByBuyer(),
            order.isConfirmedBySeller(),
            order.getCreatedAt()
        );
    }
}
