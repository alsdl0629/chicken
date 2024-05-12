package com.practice.coupon.service;

import com.practice.coupon.entity.Coupon;
import com.practice.coupon.entity.UserCoupon;
import com.practice.coupon.repository.CouponRepository;
import com.practice.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor
@Service
public class CouponServiceWithLock implements CouponService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final Lock lock;

    @Override
    public void issue(long couponId, long userId) {
        try {
            lock.lock();
            Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow();
            coupon.issue();
            couponRepository.save(coupon);
        } finally {
            lock.unlock();
        }

        userCouponRepository.save(
            new UserCoupon(couponId, userId)
        );
    }
}
