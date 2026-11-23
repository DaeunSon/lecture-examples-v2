package com.growmighty.lectures.firstday.tangledmonolith.homework;

import com.growmighty.lectures.firstday.tangledmonolith.payment.Payment;
import com.growmighty.lectures.firstday.tangledmonolith.payment.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [과제 DDD-2] 결제 상태 전이 테스트 — 스프링 없이 도는 순수 단위 테스트.
 * changeStatus(아무 상태) 대신 approve() / fail() 로만 상태가 바뀌고, READY 에서만 전이할 수 있다.
 */
class PaymentStatusTest {

    private Payment 준비된_결제() {
        return Payment.ready(BigDecimal.valueOf(10000));
    }

    @Test
    @DisplayName("준비(READY) 상태의 결제를 승인하면 PAID 가 된다")
    void 준비_상태를_승인한다() {
        // given
        Payment payment = 준비된_결제();

        // when
        payment.approve();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
    }

    @Test
    @DisplayName("준비 상태의 결제를 실패 처리하면 FAILED 가 된다")
    void 준비_상태를_실패_처리한다() {
        // given
        Payment payment = 준비된_결제();

        // when
        payment.fail();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("이미 승인된 결제는 다시 승인하거나 실패 처리할 수 없다")
    void 승인된_결제는_상태를_바꿀_수_없다() {
        // given
        Payment payment = 준비된_결제();
        payment.approve();

        // when & then
        assertThatThrownBy(payment::approve).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(payment::fail).isInstanceOf(IllegalStateException.class);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
    }

    @Test
    @DisplayName("실패한 결제는 승인할 수 없다")
    void 실패한_결제는_승인할_수_없다() {
        // given
        Payment payment = 준비된_결제();
        payment.fail();

        // when & then
        assertThatThrownBy(payment::approve).isInstanceOf(IllegalStateException.class);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }
}
