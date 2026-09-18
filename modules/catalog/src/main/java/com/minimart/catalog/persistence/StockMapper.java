package com.minimart.catalog.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StockMapper extends BaseMapper<StockPo> {

    @Update("""
            UPDATE stock
            SET reserved_quantity = reserved_quantity + #{qty}
            WHERE sku_id = #{skuId}
              AND quantity - reserved_quantity >= #{qty}
            """)
    int reserve(@Param("skuId") long skuId, @Param("qty") int qty);

    @Update("""
            UPDATE stock
            SET quantity = quantity - #{qty},
                reserved_quantity = reserved_quantity - #{qty}
            WHERE sku_id = #{skuId}
              AND reserved_quantity >= #{qty}
              AND quantity >= #{qty}
            """)
    int confirm(@Param("skuId") long skuId, @Param("qty") int qty);

    @Update("""
            UPDATE stock
            SET reserved_quantity = reserved_quantity - #{qty}
            WHERE sku_id = #{skuId}
              AND reserved_quantity >= #{qty}
            """)
    int release(@Param("skuId") long skuId, @Param("qty") int qty);

    @Update("""
            UPDATE stock
            SET quantity = quantity + #{qty}
            WHERE sku_id = #{skuId}
            """)
    int restore(@Param("skuId") long skuId, @Param("qty") int qty);

}
