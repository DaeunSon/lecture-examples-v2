package com.growmighty.lectures.firstday.tangledmonolith.seller;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Seller ↔ Product 양방향 참조를 제거했다.
 * ID 는 N 쪽인 Product 가 sellerId 로 들고 있는다.
 * - 상품은 판매자 없이 존재할 수 없지만, 판매자는 상품이 없어도 존재한다. (의존 방향: product → seller)
 * - 관계형 DB 에서 FK 는 원래 N 쪽(products.seller_id)에 있다. 1 쪽이 List 로 들면 무한히 커지는 컬렉션이 된다.
 * - "판매자의 상품 목록"은 ProductRepository.findBySellerId(...) 같은 조회로 얻으면 된다.
 */
@Entity
@Table(name = "sellers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user 도메인은 ID 로만 참조한다.
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    public static Seller create(Long userId) {
        Seller seller = new Seller();
        seller.userId = userId;
        return seller;
    }
}
