package com.minimart.catalog.application;

import com.minimart.kernel.id.SkuId;

public class SpuNotFoundException extends RuntimeException {

    public SpuNotFoundException(SkuId skuId) {
        super("SPU not found for SKU: " + skuId.value());
    }
}
