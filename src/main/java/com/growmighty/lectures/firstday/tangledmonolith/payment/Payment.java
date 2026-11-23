package com.growmighty.lectures.firstday.tangledmonolith.payment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    public static Payment ready(BigDecimal amount) {
        Payment payment = new Payment();
        payment.amount = amount;
        payment.status = PaymentStatus.READY;
        return payment;
    }

    /** 결제 승인: READY → PAID */
    public void approve() {
        transitionFromReady(PaymentStatus.PAID);
    }

    /** 결제 실패: READY → FAILED */
    public void fail() {
        transitionFromReady(PaymentStatus.FAILED);
    }

    private void transitionFromReady(PaymentStatus next) {
        if (this.status != PaymentStatus.READY) {
            throw new IllegalStateException(
                    "READY 상태에서만 " + next + " 로 바꿀 수 있습니다. 현재 상태=" + this.status);
        }
        this.status = next;
    }
}
