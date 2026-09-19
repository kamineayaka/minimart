---
status: accepted
---

# 交易模型不按湖仓事件来塑形

本仓库是电商系统源，也打算成为 `minimart-lake` 的上游，但湖仓第一期把 Order 写成一单一 SKU，且 Kafka 未定。若现在把 MiniMart 的 Order 压成「一单一个 SKU」，以后多行购买、整单支付、按行退都会和常用电商拧着，再改写模型成本很高。

因此：**Order 就是带头的多行单据**；湖仓要的扁平事件只在出站适配里映射。Kafka 是出站管道（见 [LAKE-EVENTS.md](../LAKE-EVENTS.md)），不是订单表结构。不把 lake 的示意 JSON 当作本店表。
