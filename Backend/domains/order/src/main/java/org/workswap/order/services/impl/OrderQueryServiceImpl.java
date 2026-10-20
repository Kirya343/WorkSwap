package org.workswap.order.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.workswap.order.datasource.model.Order;
import org.workswap.order.datasource.repository.OrderRepository;
import org.workswap.order.dto.OrderDTO;
import org.workswap.order.services.OrderQueryService;
import org.workswap.sso.security.dto.UserAuthData;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Profile("server")
@RequiredArgsConstructor
public class OrderQueryServiceImpl implements OrderQueryService{

    private static final Logger logger = LoggerFactory.getLogger(OrderQueryService.class);

    private final OrderRepository orderRepository;

    public OrderDTO findByChatId(Long chatId, UserAuthData authData) {
        Order order = orderRepository.findByChatId(chatId);
        logger.debug("Заказ: {}", order.getId());

        if (!orderRepository.existsByIdAndUserIsBuyerOrSeller(order.getId(), authData.sub())) {
            throw new AccessDeniedException("Вы не являетесь участником сделки");
        }

        return OrderDTO.ofOrder(order);
    }

    public OrderDTO findOrderById(String orderId, UserAuthData authData) {

        if (!orderRepository.existsByIdAndUserIsBuyerOrSeller(orderId, authData.sub())) {
            throw new AccessDeniedException("Вы не являетесь участником сделки");
        }

        Order order = orderRepository.findById(orderId).orElseThrow(
            () -> new EntityNotFoundException("Заказ не найден"));

        return OrderDTO.ofOrder(order);
    }
}
