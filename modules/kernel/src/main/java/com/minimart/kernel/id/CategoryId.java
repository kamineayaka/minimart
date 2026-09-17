package com.minimart.kernel.id;

public record CategoryId(long value) {

    public CategoryId {
        IdChecks.requirePositive(value, "CategoryId");
    }

    public static CategoryId of(long value) {
        return new CategoryId(value);
    }
}
