package com.minimart.catalog.application;

import com.minimart.catalog.persistence.SkuMapper;
import com.minimart.catalog.persistence.SkuPo;
import com.minimart.catalog.persistence.SpuMapper;
import com.minimart.catalog.persistence.SpuPo;
import com.minimart.catalog.persistence.StockMapper;
import com.minimart.catalog.persistence.StockPo;
import com.minimart.kernel.id.SkuId;
import com.minimart.kernel.id.SpuId;
import com.minimart.kernel.money.Money;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//实现 Catalog的interface，提供具体服务
@Service
public class CatalogService implements Catalog {
    
    private final SkuMapper skuMapper;
    private final SpuMapper spuMapper;
    private final StockMapper stockMapper;

    public CatalogService(SkuMapper skuMapper, SpuMapper spuMapper, StockMapper stockMapper) {
        this.skuMapper = skuMapper;
        this.spuMapper = spuMapper;
        this.stockMapper = stockMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public SkuInfo getSkuInfo(SkuId skuId) {

        Objects.requireNonNull(skuId, "skuId");

        //根据SkuId，查询SkuPo对象
        SkuPo skuPo = skuMapper.selectById(skuId.value()); 
        if(skuPo == null) {
            throw new SkuNotFoundException(skuId);
        }

        //根据SkuPo的SpuId，查询SpuPo对象
        SpuPo spuPo = spuMapper.selectById(skuPo.getSpuId());
        if (spuPo == null) {
            throw new SpuNotFoundException(skuId);
        }

        //根据SkuId，查询StockPo对象
        StockPo stockPo = stockMapper.selectById(skuId.value());

        int onHand = stockPo == null || stockPo.getQuantity() == null ? 0 : stockPo.getQuantity();
        int reserved = stockPo == null || stockPo.getReservedQuantity() == null ? 0 : stockPo.getReservedQuantity();
        int available = Math.max(0, onHand - reserved);

        //返回SkuInfo对象
        return new SkuInfo(
            skuId,
            SpuId.of(spuPo.getId()),
            skuPo.getSpec(),
            spuPo.getName(),
            Money.ofCents(skuPo.getPriceCents()),
            available
        );
    }

    //覆写reserve，confirm，release，restore方法
    @Override
    @Transactional
    public void reserve(SkuId skuId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive, got: " + quantity);
        }
        if (stockMapper.reserve(skuId.value(), quantity) == 0) {
            SkuInfo sku = getSkuInfo(skuId);
            throw new InsufficientStockException(skuId, quantity, sku.availableQuantity());
        }
    }
    @Override
    @Transactional
    public void confirm(SkuId skuId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive, got: " + quantity);
        }
        if (stockMapper.confirm(skuId.value(), quantity) == 0) {
            getSkuInfo(skuId); // 不存在则抛 SkuNotFoundException
            throw new StockOperationException("confirm", skuId, quantity);
        }
    }
    @Override
    @Transactional
    public void release(SkuId skuId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive, got: " + quantity);
        }
        if (stockMapper.release(skuId.value(), quantity) == 0) {
            getSkuInfo(skuId); // 不存在则抛 SkuNotFoundException
            throw new StockOperationException("release", skuId, quantity);
        }
    }
    @Override
    @Transactional
    public void restore(SkuId skuId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive, got: " + quantity);
        }
        if (stockMapper.restore(skuId.value(), quantity) == 0) {
            getSkuInfo(skuId); // 不存在则抛 SkuNotFoundException
            throw new StockOperationException("restore", skuId, quantity);
        }
    }

}