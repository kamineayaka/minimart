package com.minimart.catalog.application;

import com.minimart.kernel.id.SkuId;
import com.minimart.kernel.id.SpuId;
import com.minimart.kernel.money.Money;

public record SkuInfo(
        SkuId skuId,
        SpuId spuId,
        String spec,
        String spuName,
        Money price,
        int availableQuantity
) {
    public boolean sellable() {
        return availableQuantity > 0;
    }
}