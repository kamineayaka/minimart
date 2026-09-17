-- catalog：Category / SPU / SKU / Stock

CREATE TABLE category (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类主键',
    parent_id  BIGINT       NULL COMMENT '父分类；根节点为空',
    name       VARCHAR(64)  NOT NULL COMMENT '分类名称',
    deleted    TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删，1 已删',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category (id)
) COMMENT '商品分类';

CREATE TABLE spu (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'SPU 主键',
    category_id BIGINT        NOT NULL COMMENT '所属分类',
    name        VARCHAR(128)  NOT NULL COMMENT '商品名称（列表页展示）',
    deleted     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删，1 已删',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    CONSTRAINT fk_spu_category FOREIGN KEY (category_id) REFERENCES category (id)
) COMMENT '货架商品（SPU），下面可挂多个 SKU';

CREATE TABLE sku (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'SKU 主键',
    spu_id      BIGINT        NOT NULL COMMENT '所属 SPU',
    spec        VARCHAR(128)  NOT NULL COMMENT '规格，如颜色/尺码',
    price_cents BIGINT        NOT NULL COMMENT '售价，单位：分',
    deleted     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删，1 已删',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    CONSTRAINT fk_sku_spu FOREIGN KEY (spu_id) REFERENCES spu (id)
) COMMENT '可售单元';

CREATE TABLE stock (
    sku_id             BIGINT   NOT NULL COMMENT '对应 SKU',
    quantity           INT      NOT NULL COMMENT '账面总量',
    reserved_quantity  INT      NOT NULL DEFAULT 0 COMMENT '已预占数量；可售 = quantity - reserved_quantity',
    updated_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (sku_id),
    CONSTRAINT fk_stock_sku FOREIGN KEY (sku_id) REFERENCES sku (id)
) COMMENT 'SKU 库存';
