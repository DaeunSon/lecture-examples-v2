package com.growmighty.lectures.firstday.tangledmonolith.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    // product 도메인은 ID 로만 참조한다.
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 주문 당시 상품 스냅샷: 이후 상품명·가격이 바뀌어도 주문 내역은 그대로여야 한다.
    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal productPrice;

    @Column(nullable = false)
    private Integer quantity;

    public static OrderItem create(Long productId, String productName, BigDecimal productPrice, int quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.productId = productId;
        orderItem.productName = productName;
        orderItem.productPrice = productPrice;
        orderItem.quantity = quantity;

        return orderItem;
    }

    public BigDecimal getLineAmount() {
        return productPrice.multiply(BigDecimal.valueOf(quantity));
    }

    void assignOrder(Order order) {
        this.order = order;
    }
}
