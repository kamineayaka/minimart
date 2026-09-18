package com.minimart.catalog.application;

import com.minimart.kernel.id.SkuId;

public class SkuNotFoundException extends RuntimeException {

    public SkuNotFoundException(SkuId skuId) {
        super("SKU not found: " + skuId.value());
    }
}
