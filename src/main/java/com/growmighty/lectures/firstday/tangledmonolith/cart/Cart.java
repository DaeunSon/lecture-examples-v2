package com.growmighty.lectures.firstday.tangledmonolith.cart;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 장바구니 애그리거트 루트.
 * CartItem 은 Cart 를 통해서만 생성·변경·삭제된다. 밖에서 만든 항목을 넣을 수 없다.
 *
 * 불변식
 * - 항목 수량은 1 이상
 * - 같은 상품은 한 줄만 존재 (다시 담으면 수량 합산)
 * - 담을 수 있는 상품 종류는 최대 {@link #MAX_ITEM_KINDS}종
 */
@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {
    public static final int MAX_ITEM_KINDS = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @Column(nullable = false)
    private Long userId;

    public static Cart create(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId 는 필수입니다.");
        }
        Cart cart = new Cart();
        cart.userId = userId;
        return cart;
    }

    public void addItem(Long productId, int quantity) {
        Optional<CartItem> existing = findItem(productId);
        if (existing.isPresent()) {
            existing.get().increaseQuantity(quantity);
            return;
        }

        if (items.size() >= MAX_ITEM_KINDS) {
            throw new IllegalStateException("장바구니에는 최대 " + MAX_ITEM_KINDS + "종의 상품만 담을 수 있습니다.");
        }
        items.add(CartItem.create(this, productId, quantity));
    }

    public void changeQuantity(Long productId, int quantity) {
        getItemOrThrow(productId).changeQuantity(quantity);
    }

    public void removeItem(Long productId) {
        items.remove(getItemOrThrow(productId));
    }

    public void clear() {
        this.items.clear();
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    private Optional<CartItem> findItem(Long productId) {
        return items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }

    private CartItem getItemOrThrow(Long productId) {
        return findItem(productId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 없는 상품입니다. productId=" + productId));
    }
}
