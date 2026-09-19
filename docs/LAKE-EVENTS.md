# 入湖事件

本文件是 MiniMart 与 `minimart-lake` 的出站合同。OLTP 仍是多行 Order，见 [ADR-0001](adr/0001-oltp-independent-of-lake.md)。

**状态：尚未实现。** 骨架阶段只在 compose 中提供 Kafka，应用未接入。

## Topic

`orders.events`

## 映射

支付成功后，order-service 在同一事务写入 outbox；publisher 按 **OrderLine** 拆成多条消息（湖仓一期一单一 SKU），而不是把订单表压扁。

| 店内 | Kafka `eventType` |
|------|-------------------|
| Order 变为 `PAID` | 每行一条 `OrderLinePaid` |
| Order 变为 `REFUNDED` | 每行一条 `OrderLineRefunded` |
| `PENDING_PAY` → `CANCELLED` | 一期可不入湖 |

`eventId` 按行稳定生成，便于湖仓幂等。金额字段为整数分；若湖仓要元，只在 JSON 边界转换。

示意（未实现，勿当 API）：

```json
{
  "eventId": "order-1001-sku-1-paid",
  "eventType": "OrderLinePaid",
  "orderId": 1001,
  "skuId": 1,
  "qty": 1,
  "priceCents": 3900,
  "payCents": 3900,
  "userId": 7,
  "occurredAt": "2026-09-19T02:00:00Z"
}
```
