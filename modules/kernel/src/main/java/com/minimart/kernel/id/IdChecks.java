package com.minimart.kernel.id;

final class IdChecks {

    private IdChecks() {}

    static long requirePositive(long value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive, got: " + value);
        }
        return value;
    }
}
