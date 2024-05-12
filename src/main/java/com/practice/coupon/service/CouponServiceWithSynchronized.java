package com.practice.coupon.service;

import com.practice.coupon.entity.Coupon;
import com.practice.coupon.entity.UserCoupon;
import com.practice.coupon.repository.CouponRepository;
import com.practice.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CouponServiceWithSynchronized implements CouponService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    /**
     * saveAndFlush를 사용해도 트랜잭션 격리 수준 때문에 똑같이 레이스 컨디션 발생
     * READ_UNCOMMITTED에서 성공
     */
    @Override
    @Transactional
//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void issue(long couponId, long userId) {
        synchronized (this) {
            Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow();
            coupon.issue();
            couponRepository.saveAndFlush(coupon);
        }

        userCouponRepository.save(
            new UserCoupon(couponId, userId)
        );
    }
}
