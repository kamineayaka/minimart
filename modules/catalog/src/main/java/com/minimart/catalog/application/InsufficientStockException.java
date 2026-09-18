package com.minimart.catalog.application;

import com.minimart.kernel.id.SkuId;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(SkuId skuId, int requested, int available) {
        super("insufficient stock for SKU " + skuId.value()
                + ", requested " + requested
                + ", available " + available);
    }
}
