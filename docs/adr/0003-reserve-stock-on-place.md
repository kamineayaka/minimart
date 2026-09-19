---
status: accepted
---

# 下单时预占库存，而不是支付时才扣

常见另一种做法是下单不碰库存、付款成功再扣，实现简单，但 `PENDING_PAY` 期间会超卖，抢最后一件的场景无法成立。本店 v1 要把超卖当成必须处理的规则，而不是以后再补。

因此：创建 `PENDING_PAY` 的 Order 时由 **product-service** 预占；Payment 成功则确认扣减；取消或超时则释放。order-service 内的模拟支付不直接改 Stock。售完即无法进入 `PENDING_PAY`。
