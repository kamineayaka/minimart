package com.minimart.kernel.id;

public record UserId(long value) {

    public UserId {
        IdChecks.requirePositive(value, "UserId");
    }

    public static UserId of(long value) {
        return new UserId(value);
    }
}
