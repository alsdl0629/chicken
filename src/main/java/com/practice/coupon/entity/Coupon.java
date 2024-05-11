package com.practice.coupon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Coupon {
    @Id
    private Long id;

    private String name;

    private int totalQuantity;

    private int issuedQuantity;

    public Coupon(Long id, String name, int totalQuantity, int issuedQuantity) {
        this.id = id;
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = issuedQuantity;
    }

    public void issue() {
        if (issuedQuantity >= totalQuantity) {
            throw new RuntimeException("더 이상 쿠폰을 발급할 수 없습니다.");
        }
        issuedQuantity++;
    }
}
