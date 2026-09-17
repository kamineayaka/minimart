package com.minimart.catalog.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("stock")
public class StockPo {

    @TableId(type = IdType.INPUT)
    private Long skuId;
    private Integer quantity;
    private Integer reservedQuantity;
    private LocalDateTime updatedAt;
}
