package com.minimart.catalog.application;

import com.minimart.kernel.id.SkuId;

public class StockOperationException extends RuntimeException {

    public StockOperationException(String operation, SkuId skuId, int quantity) {
        super(operation + " failed for SKU " + skuId.value() + ", quantity " + quantity);
    }
}
