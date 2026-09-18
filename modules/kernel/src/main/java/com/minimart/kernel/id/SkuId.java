package com.minimart.kernel.id;

public record SkuId(long value) {

    public SkuId {
        IdChecks.requirePositive(value, "SkuId");
    }

    public static SkuId of(long value) {
        return new SkuId(value);
    }
}
