package com.growmighty.lectures.firstday.tangledmonolith.homework;

import com.growmighty.lectures.firstday.tangledmonolith.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [과제 DDD-2] 상품 재고 규칙 테스트 — 스프링 없이 도는 순수 단위 테스트.
 */
class ProductStockTest {

    private Product 재고가_10개인_상품() {
        return Product.create(1L, "원목 4인용 식탁", BigDecimal.valueOf(259000), 10, "테스트 상품");
    }

    @Test
    @DisplayName("재고 안에서 차감하면 재고가 줄어든다")
    void 재고를_차감한다() {
        // given
        Product product = 재고가_10개인_상품();

        // when
        product.decreaseStock(3);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("재고와 같은 수량을 차감하면 재고가 0 이 된다")
    void 재고를_전부_차감한다() {
        // given
        Product product = 재고가_10개인_상품();

        // when
        product.decreaseStock(10);

        // then
        assertThat(product.getStockQuantity()).isZero();
    }

    @Test
    @DisplayName("재고보다 많이 차감하면 예외가 나고 재고는 그대로다")
    void 재고_부족이면_예외() {
        // given
        Product product = 재고가_10개인_상품();

        // when & then
        assertThatThrownBy(() -> product.decreaseStock(11))
                .isInstanceOf(IllegalStateException.class);
        assertThat(product.getStockQuantity()).isEqualTo(10);
    }

    @ParameterizedTest(name = "수량 {0}")
    @ValueSource(ints = {0, -3})
    @DisplayName("0 이하의 수량은 차감할 수 없다")
    void 수량이_0_이하면_예외(int quantity) {
        // given
        Product product = 재고가_10개인_상품();

        // when & then
        assertThatThrownBy(() -> product.decreaseStock(quantity))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(product.getStockQuantity()).isEqualTo(10);
    }
}
