-- 开发样例。主键固定，便于手工验 getSkuInfo / 预占 / 抢最后一件。

INSERT INTO category (id, parent_id, name) VALUES
    (1, NULL, '服装'),
    (2, 1, 'T恤');

INSERT INTO spu (id, category_id, name) VALUES
    (1, 2, '基础棉T'),
    (2, 2, '限量短袖');

INSERT INTO sku (id, spu_id, spec, price_cents) VALUES
    (1, 1, '白色 / M', 3900),
    (2, 1, '白色 / L', 3900),
    (3, 2, '黑色 / M', 5900);

INSERT INTO stock (sku_id, quantity, reserved_quantity) VALUES
    (1, 10, 0),  -- 可售 10
    (2, 0, 0),   -- 售罄
    (3, 1, 0);   -- 最后一件
