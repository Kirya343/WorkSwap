package org.workswap.order.services;

import org.workswap.order.dto.OrderDTO;
import org.workswap.sso.security.dto.UserAuthData;

public interface OrderQueryService {
    
    OrderDTO findByChatId(Long chatId, UserAuthData authData);
    OrderDTO findOrderById(String orderId, UserAuthData authData);
}
