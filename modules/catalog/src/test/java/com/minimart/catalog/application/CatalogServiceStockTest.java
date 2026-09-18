package com.minimart.catalog.application;

import com.minimart.catalog.persistence.SkuMapper;
import com.minimart.catalog.persistence.SkuPo;
import com.minimart.catalog.persistence.SpuMapper;
import com.minimart.catalog.persistence.SpuPo;
import com.minimart.catalog.persistence.StockMapper;
import com.minimart.catalog.persistence.StockPo;
import com.minimart.kernel.id.SkuId;
import com.minimart.kernel.money.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceStockTest {

    private static final SkuId SKU_ID = SkuId.of(1);

    @Mock
    private SkuMapper skuMapper;
    @Mock
    private SpuMapper spuMapper;
    @Mock
    private StockMapper stockMapper;

    @InjectMocks
    private CatalogService catalog;

    @Test
    void getSkuInfo_computesAvailableAndSellable() {
        stubExistingSku(10, 2);

        SkuInfo info = catalog.getSkuInfo(SKU_ID);

        assertEquals(SKU_ID, info.skuId());
        assertEquals(1L, info.spuId().value());
        assertEquals("白色 / M", info.spec());
        assertEquals("基础棉T", info.spuName());
        assertEquals(Money.ofCents(3900), info.price());
        assertEquals(8, info.availableQuantity());
        assertTrue(info.sellable());
    }

    @Test
    void getSkuInfo_soldOutIsNotSellable() {
        stubExistingSku(0, 0);

        SkuInfo info = catalog.getSkuInfo(SKU_ID);

        assertEquals(0, info.availableQuantity());
        assertFalse(info.sellable());
    }

    @Test
    void getSkuInfo_missingSku() {
        when(skuMapper.selectById(1L)).thenReturn(null);

        assertThrows(SkuNotFoundException.class, () -> catalog.getSkuInfo(SKU_ID));
    }

    @Test
    void reserve_successDoesNotReReadSku() {
        when(stockMapper.reserve(1L, 2)).thenReturn(1);

        catalog.reserve(SKU_ID, 2);

        verify(stockMapper).reserve(1L, 2);
        verify(skuMapper, never()).selectById(any());
    }

    @Test
    void reserve_rejectsNonPositiveQuantity() {
        assertThrows(IllegalArgumentException.class, () -> catalog.reserve(SKU_ID, 0));
        assertThrows(IllegalArgumentException.class, () -> catalog.reserve(SKU_ID, -1));
        verify(stockMapper, never()).reserve(anyLong(), anyInt());
    }

    @Test
    void reserve_insufficientStockIncludesAvailable() {
        when(stockMapper.reserve(1L, 3)).thenReturn(0);
        stubExistingSku(10, 8);

        InsufficientStockException ex = assertThrows(
                InsufficientStockException.class,
                () -> catalog.reserve(SKU_ID, 3));

        assertTrue(ex.getMessage().contains("requested 3"));
        assertTrue(ex.getMessage().contains("available 2"));
    }

    @Test
    void reserve_missingSku() {
        when(stockMapper.reserve(1L, 1)).thenReturn(0);
        when(skuMapper.selectById(1L)).thenReturn(null);

        assertThrows(SkuNotFoundException.class, () -> catalog.reserve(SKU_ID, 1));
    }

    @Test
    void confirm_success() {
        when(stockMapper.confirm(1L, 2)).thenReturn(1);

        catalog.confirm(SKU_ID, 2);

        verify(stockMapper).confirm(1L, 2);
    }

    @Test
    void confirm_zeroRowsWhenSkuExists() {
        when(stockMapper.confirm(1L, 2)).thenReturn(0);
        stubExistingSku(10, 0);

        StockOperationException ex = assertThrows(
                StockOperationException.class,
                () -> catalog.confirm(SKU_ID, 2));

        assertTrue(ex.getMessage().startsWith("confirm"));
    }

    @Test
    void release_success() {
        when(stockMapper.release(1L, 2)).thenReturn(1);

        catalog.release(SKU_ID, 2);

        verify(stockMapper).release(1L, 2);
    }

    @Test
    void restore_success() {
        when(stockMapper.restore(1L, 2)).thenReturn(1);

        catalog.restore(SKU_ID, 2);

        verify(stockMapper).restore(1L, 2);
    }

    @Test
    void restore_zeroRowsWhenSkuExists() {
        when(stockMapper.restore(1L, 2)).thenReturn(0);
        stubExistingSku(8, 0);

        assertThrows(StockOperationException.class, () -> catalog.restore(SKU_ID, 2));
    }

    private void stubExistingSku(int onHand, int reserved) {
        SkuPo sku = new SkuPo();
        sku.setId(1L);
        sku.setSpuId(1L);
        sku.setSpec("白色 / M");
        sku.setPriceCents(3900L);

        SpuPo spu = new SpuPo();
        spu.setId(1L);
        spu.setName("基础棉T");

        StockPo stock = new StockPo();
        stock.setSkuId(1L);
        stock.setQuantity(onHand);
        stock.setReservedQuantity(reserved);

        when(skuMapper.selectById(1L)).thenReturn(sku);
        when(spuMapper.selectById(1L)).thenReturn(spu);
        when(stockMapper.selectById(1L)).thenReturn(stock);
    }
}
