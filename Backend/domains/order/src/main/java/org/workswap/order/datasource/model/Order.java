package org.workswap.order.datasource.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.workswap.chat.datasource.model.Chat;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.order.enums.OrderStatus;
import org.workswap.user.datasource.model.User;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "purchase_orders")
public class Order {

    private static final char[] ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    public Order(
        Listing listing,
        User buyer,
        User seller
    ) {
        this.listing = listing;
        this.buyer = buyer;
        this.seller = seller;
    }
    
    @Id
    @Column(length = 20, nullable = false, unique = true)
    private String id = NanoIdUtils.randomNanoId(
        NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
        ALPHANUMERIC,
        20
    );

    @ManyToOne
    private Listing listing;

    @ManyToOne
    private User buyer;

    @ManyToOne
    private User seller;

    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CREATED;

    @Setter
    private boolean confirmedByBuyer = false;

    @Setter
    private boolean confirmedBySeller = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Setter
    private LocalDateTime completedAt;

    @Setter
    @OneToOne
    private Chat chat;
}