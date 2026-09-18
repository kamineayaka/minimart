package com.minimart.catalog.application;

import com.minimart.kernel.id.SkuId;

//catalog 对外能力接口。cart / order 只依赖本接口，不访问 persistence。
public interface Catalog {
    
    SkuInfo getSkuInfo(SkuId skuId);
    void reserve(SkuId skuId, int quantity);
    void confirm(SkuId skuId, int quantity);
    void release(SkuId skuId, int quantity);
    void restore(SkuId skuId, int quantity);

}
