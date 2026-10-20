package org.workswap.order.services;

import org.workswap.listing.datasource.model.Listing;
import org.workswap.order.datasource.model.Order;
import org.workswap.order.dto.OrderDTO;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.user.datasource.model.User;

public interface OrderCommandService {
    
    OrderDTO getOrderDTO(UserAuthData authData, Long listingId);
    Order getOrCreateOrder(User buyer, User seller, Listing listing);

    void markConfirmed(String orderId, Long userId);
}
