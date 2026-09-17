package com.minimart.kernel.id;

public record OrderId(long value) {

    public OrderId {
        IdChecks.requirePositive(value, "OrderId");
    }

    public static OrderId of(long value) {
        return new OrderId(value);
    }
}
