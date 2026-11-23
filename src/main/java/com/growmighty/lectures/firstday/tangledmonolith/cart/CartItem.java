package com.growmighty.lectures.firstday.tangledmonolith.cart;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 같은 cart 애그리거트 내부이므로 객체 참조를 유지한다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // product 도메인은 ID 로만 참조한다. 가격·이름은 담는 시점이 아니라 주문 시점에 조회한다.
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    public static CartItem create(Long productId, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.productId = productId;
        cartItem.quantity = quantity;
        return cartItem;
    }

    void assignCart(Cart cart) {
        this.cart = cart;
    }
}
