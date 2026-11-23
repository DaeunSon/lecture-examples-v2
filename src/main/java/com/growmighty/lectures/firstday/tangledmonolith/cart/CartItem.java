package com.growmighty.lectures.firstday.tangledmonolith.cart;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 장바구니 항목. 생성·수량 변경은 같은 패키지의 Cart(애그리거트 루트)만 할 수 있다.
 */
@Entity
@Table(name = "cart_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    static CartItem create(Cart cart, Long productId, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("productId 는 필수입니다.");
        }
        validateQuantity(quantity);
        CartItem cartItem = new CartItem();
        cartItem.cart = cart;
        cartItem.productId = productId;
        cartItem.quantity = quantity;
        return cartItem;
    }

    void increaseQuantity(int amount) {
        validateQuantity(amount);
        this.quantity += amount;
    }

    void changeQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    private static void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다. quantity=" + quantity);
        }
    }
}
