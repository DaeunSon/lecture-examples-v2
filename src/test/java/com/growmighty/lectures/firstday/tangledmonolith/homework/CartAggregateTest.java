package com.growmighty.lectures.firstday.tangledmonolith.homework;

import com.growmighty.lectures.firstday.tangledmonolith.cart.Cart;
import com.growmighty.lectures.firstday.tangledmonolith.cart.CartItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [과제 DDD-2] 장바구니 애그리거트 불변식 테스트 — 스프링 없이 도는 순수 단위 테스트.
 */
class CartAggregateTest {

    private static final Long USER_ID = 1L;
    private static final Long PRODUCT_A = 1L;
    private static final Long PRODUCT_B = 2L;

    @Test
    @DisplayName("새 상품을 담으면 항목이 하나 추가된다")
    void 새_상품을_담으면_항목이_추가된다() {
        // given: 빈 장바구니
        Cart cart = Cart.create(USER_ID);

        // when : addItem(1L, 2)
        cart.addItem(PRODUCT_A, 2);

        // then : 항목 1개, 수량 2
        assertThat(cart.getItems()).hasSize(1);
        CartItem item = cart.getItems().get(0);
        assertThat(item.getProductId()).isEqualTo(PRODUCT_A);
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getCart()).isSameAs(cart);
    }

    @Test
    @DisplayName("같은 상품을 다시 담으면 새 항목이 아니라 수량이 합쳐진다")
    void 같은_상품을_담으면_수량이_합쳐진다() {
        // given
        Cart cart = Cart.create(USER_ID);
        cart.addItem(PRODUCT_A, 2);

        // when
        cart.addItem(PRODUCT_A, 3);

        // then
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(5);
    }

    @ParameterizedTest(name = "수량 {0}")
    @ValueSource(ints = {0, -1})
    @DisplayName("수량이 0 이하이면 담을 수 없다")
    void 수량이_0_이하면_예외(int quantity) {
        // given
        Cart cart = Cart.create(USER_ID);

        // when & then
        assertThatThrownBy(() -> cart.addItem(PRODUCT_A, quantity))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    @DisplayName("이미 담긴 상품에 0 이하 수량을 더하면 예외가 나고 수량은 그대로다")
    void 담긴_상품에_0_이하를_더하면_예외() {
        // given
        Cart cart = Cart.create(USER_ID);
        cart.addItem(PRODUCT_A, 2);

        // when & then
        assertThatThrownBy(() -> cart.addItem(PRODUCT_A, -2))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("담긴 상품의 수량을 바꿀 수 있다")
    void 수량을_변경한다() {
        // given
        Cart cart = Cart.create(USER_ID);
        cart.addItem(PRODUCT_A, 2);

        // when
        cart.changeQuantity(PRODUCT_A, 7);

        // then
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("수량을 0 이하로 바꿀 수 없다")
    void 수량을_0_이하로_변경하면_예외() {
        // given
        Cart cart = Cart.create(USER_ID);
        cart.addItem(PRODUCT_A, 2);

        // when & then
        assertThatThrownBy(() -> cart.changeQuantity(PRODUCT_A, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("장바구니에 없는 상품의 수량을 바꾸면 예외가 난다")
    void 없는_상품의_수량_변경은_예외() {
        // given
        Cart cart = Cart.create(USER_ID);
        cart.addItem(PRODUCT_A, 2);

        // when & then
        assertThatThrownBy(() -> cart.changeQuantity(PRODUCT_B, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("담긴 상품을 뺄 수 있다")
    void 상품을_뺀다() {
        // given
        Cart cart = Cart.create(USER_ID);
        cart.addItem(PRODUCT_A, 2);
        cart.addItem(PRODUCT_B, 1);

        // when
        cart.removeItem(PRODUCT_A);

        // then
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getProductId()).isEqualTo(PRODUCT_B);
    }

    @Test
    @DisplayName("장바구니에 없는 상품을 빼면 예외가 난다")
    void 없는_상품을_빼면_예외() {
        // given
        Cart cart = Cart.create(USER_ID);

        // when & then
        assertThatThrownBy(() -> cart.removeItem(PRODUCT_A))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("항목 목록은 밖에서 직접 바꿀 수 없다")
    void 항목_목록은_밖에서_수정할_수_없다() {
        // given
        Cart cart = Cart.create(USER_ID);
        cart.addItem(PRODUCT_A, 2);

        // when & then
        assertThatThrownBy(() -> cart.getItems().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("[도전] 상품 종류가 50종이 차면 새 상품은 담을 수 없다")
    void 상품_종류가_상한을_넘으면_예외() {
        // given: 50종이 담긴 장바구니
        Cart cart = Cart.create(USER_ID);
        for (long productId = 1; productId <= Cart.MAX_ITEM_KINDS; productId++) {
            cart.addItem(productId, 1);
        }

        // when & then
        assertThatThrownBy(() -> cart.addItem(Cart.MAX_ITEM_KINDS + 1L, 1))
                .isInstanceOf(IllegalStateException.class);
        assertThat(cart.getItems()).hasSize(Cart.MAX_ITEM_KINDS);
    }

    @Test
    @DisplayName("[도전] 50종이 차 있어도 이미 담긴 상품의 수량은 늘릴 수 있다")
    void 상한에서도_기존_상품은_수량_합산된다() {
        // given
        Cart cart = Cart.create(USER_ID);
        for (long productId = 1; productId <= Cart.MAX_ITEM_KINDS; productId++) {
            cart.addItem(productId, 1);
        }

        // when
        cart.addItem(PRODUCT_A, 4);

        // then
        assertThat(cart.getItems()).hasSize(Cart.MAX_ITEM_KINDS);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(5);
    }
}
