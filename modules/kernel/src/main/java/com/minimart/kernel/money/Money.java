package com.minimart.kernel.money;

import java.util.Objects;

/* 金额，单位：分（人民币）。存储与算术均用整数，见 ADR-0004。 */

public record Money(long cents) {

    public static final Money ZERO = new Money(0);
    
    public Money {
        if (cents < 0) {
            throw new IllegalArgumentException("cents must be non-negative, got: " + cents);
        }
    }

    public static Money ofCents(long cents) {
        return new Money(cents);
    }

    public static Money ofYuan(long yuan) {
        return new Money(yuan * 100);
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "other");
        return new Money(this.cents + other.cents);
    }

    public Money subtract(Money other) {
        Objects.requireNonNull(other, "other");
        long result = this.cents - other.cents;
        if (result < 0) {
            throw new IllegalArgumentException("money would become negative");
        }
        return new Money(result);
    }

    public Money multiply(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive, got: " + quantity);
        }
        return new Money(this.cents * quantity);
    }

}
