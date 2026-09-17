package com.minimart.kernel.id;

public record SpuId(long value) {

    public SpuId {
        IdChecks.requirePositive(value, "SpuId");
    }

    public static SpuId of(long value) {
        return new SpuId(value);
    }
}
