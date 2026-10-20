package org.workswap.order.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.order.dto.OrderDTO;
import org.workswap.order.services.OrderCommandService;
import org.workswap.order.services.OrderQueryService;
import org.workswap.sso.security.annotations.controllers.Authenticated;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    @GetMapping("/{orderId}")
    @Authenticated
    public OrderDTO getOrderById(
        @AuthUser UserAuthData authData,
        @PathVariable String orderId
    ) {
        return orderQueryService.findOrderById(orderId, authData);
    }

    @GetMapping("/{chatId}/chat")
    @Authenticated
    public OrderDTO getOrderByChat(
        @AuthUser UserAuthData authData,
        @PathVariable Long chatId
    ) {
        return orderQueryService.findByChatId(chatId, authData);
    }

    @GetMapping
    @Authenticated
    public OrderDTO getOrCreateOrder(
        @AuthUser UserAuthData authData,
        @RequestParam Long listingId
    ) {
        return orderCommandService.getOrderDTO(authData, listingId);
    }
}
