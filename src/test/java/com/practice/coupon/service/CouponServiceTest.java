package com.practice.coupon.service;

import com.practice.coupon.entity.Coupon;
import com.practice.coupon.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CouponServiceTest {
    @Autowired
    private CouponServiceWithSynchronized couponService;
    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("한 명의 유저가 쿠폰을 발행한다")
    @Test
    void issueCouponByOneUser() {
        // given
        long userId = 1L;
        long couponId = 1L;
        couponRepository.saveAndFlush(
            new Coupon(couponId, "치킨 할인 쿠폰", 100, 0)
        );

        // when
        couponService.issue(couponId, userId);

        // then
        Coupon coupon = couponRepository.findById(couponId).orElseThrow();
        assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
    }

    @DisplayName("쿠폰 발행 수가 쿠폰 개수보다 많아 발급할 수 없다")
    @Test
    void canNotIssueCoupon() {
        // given
        long userId = 1L;
        long couponId = 1L;
        couponRepository.saveAndFlush(
            new Coupon(couponId, "치킨 할인 쿠폰", 100, 100)
        );

        // when & then
        assertThatThrownBy(() -> couponService.issue(couponId, userId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("더 이상 쿠폰을 발급할 수 없습니다.");
    }

    @DisplayName("동시에 여러명의 유저가 쿠폰을 발행한다")
    @Test
    void issueCouponByUsers() throws InterruptedException {
        // given
        long userId = 1L;
        long couponId = 1L;
        couponRepository.saveAndFlush(
            new Coupon(couponId, "치킨 할인 쿠폰", 100, 0)
        );

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // when
        IntStream.range(0, 100)
            .forEach(n -> executorService.execute(() -> {
                    try {
                        couponService.issue(couponId, userId);
                    } finally {
                        countDownLatch.countDown();
                    }
                })
            );
        countDownLatch.await();

        // then
        Coupon coupon = couponRepository.findById(couponId).orElseThrow();
        assertThat(coupon.getIssuedQuantity()).isEqualTo(100);
    }
}