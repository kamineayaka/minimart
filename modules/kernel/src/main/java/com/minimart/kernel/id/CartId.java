package com.minimart.kernel.id;

public record CartId(long value) {

    public CartId {
        IdChecks.requirePositive(value, "CartId");
    }

    public static CartId of(long value) {
        return new CartId(value);
    }
}
