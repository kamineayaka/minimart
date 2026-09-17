# MiniMart v1 契约

本文件约束 **v1 行为**：做什么、不做什么、状态怎么迁、哪些情况必须成立。用语以根目录 [CONTEXT.md](../CONTEXT.md) 为准。模块谁拥有什么见 [MODULES.md](MODULES.md)。

这是交易系统的契约，不是湖仓表结构，也不是接口清单。

## 本仓库的位置

MiniMart 是 B/S 电商的系统源。`minimart-lake` 将来消费本店产生的订单变化，但 **写模型按电商常用形态设计**，不按湖仓当前的一单一 SKU 事件来压扁 Order。入湖映射未定，v1 不依赖 Kafka。

## 做与不做

**做**：单店 B2C；Category / SPU / SKU；Cart；多行 Order；模拟 Payment；Stock 在下单时预占。

**不做**：多店/商家入驻、优惠券、评价、真实物流与承运商、仓配、部分退、秒杀、SPU 历史售价、对接 Kafka / CDC。

v1 可以有 `SHIPPED` / `COMPLETED` 两个状态，但没有独立的发货单实体。

## Order 状态

```
PENDING_PAY → PAID → SHIPPED → COMPLETED
     │          │
     ▼          ▼
 CANCELLED   REFUNDED
```

| 从 | 到 | 何时 |
|----|----|------|
| （新建） | `PENDING_PAY` | 结算成功，Order 已创建，Stock 已预占 |
| `PENDING_PAY` | `PAID` | 且仅当该 Order 的 Payment 成功 |
| `PENDING_PAY` | `CANCELLED` | User 取消，或超时未支付 |
| `PAID` | `SHIPPED` | 标记发货（v1 可手工/接口，无承运商） |
| `SHIPPED` | `COMPLETED` | 标记完成 |
| `PAID` | `REFUNDED` | 整单退款成功 |

其余迁移非法。已发货（`SHIPPED`）及之后 **不做退款**——部分退和售后退款都不是 v1。

一笔 Order 在任一时刻只有一个状态。行上不单独做状态。

## Payment 状态

一笔 Order 在 v1 **最多一笔** Payment。

| 状态 | 含义 |
|------|------|
| `PENDING` | 已发起、尚未成功 |
| `SUCCEEDED` | 已收到款 |
| `FAILED` | 失败且不再作为成功入口 |

`SUCCEEDED` 不可改为失败来「撤销支付」；撤销已支付 Order 走 Refund，把 Order 打到 `REFUNDED`。

重复发起支付：若已有 `PENDING` 或 `SUCCEEDED`，不得再开第二笔。回调重复到达时，必须幂等，不得把已 `PAID` 的 Order 再迁一次。

## 必须成立

1. 一笔 Order **至少一行** OrderLine；禁止无货空单。
2. 同一 Order 内，同一 SKU **只能一行**，数量合并。
3. OrderLine 的单价、行金额按下单瞬间的 SKU 售价快照；之后改 SKU 价不影响已下的单。
4. 结算时把当时的 Address **抄写**到 Order；之后 User 改地址不影响该单。
5. Cart 结算时 **生成新的 Order 与 OrderLine**，然后清空（或删除）对应 CartLine；禁止把 CartLine 改写成 OrderLine。
6. 只有 Payment 变为 `SUCCEEDED`，Order 才能变为 `PAID`。禁止只改订单状态、没有对应成功 Payment。
7. **下单预占 Stock，支付确认扣减，取消或超时释放**。禁止支付成功才第一次碰库存（v1 用预占避免超卖）。
8. 预占数量不得超过当时可售 Stock；两个 User 抢最后一件时，只有一单能进入 `PENDING_PAY`。
9. Refund 只针对整单，且只从 `PAID` 出发；退款后应把该单预占/已扣的 Stock 按行还回去。
10. 金额在本上下文中是人民币；存储与计算用整数分，见 [ADR-0004](adr/0004-money-as-integer-cents.md)。

## 场景（验收用）

**两件不同 SKU，付一次款**  
一个 User、一个 Cart 里两行 → 一笔 Order、两行 OrderLine、一笔 Payment。Order 经 `PENDING_PAY` 到 `PAID`。两件 SKU 的 Stock 都先预占再扣减。

**未支付取消**  
`PENDING_PAY` → `CANCELLED`。预占释放。没有 `SUCCEEDED` 的 Payment。不得留下「已支付」痕迹。

**支付后整单退**  
`PAID` → `REFUNDED`。整单行上的 SKU 数量归还 Stock。不做「只退其中一行」。

**改价不影响已下单**  
SKU 售价从 10 元改为 12 元；已存在 OrderLine 仍是 10 元（1000 分）。

**抢最后一件**  
Stock 为 1。两个 User 同时结算同一 SKU：一单 `PENDING_PAY` 并预占 1，另一单失败且不出现 Order，或出现后不得预占成功。

**支付回调打两次**  
Payment 已是 `SUCCEEDED`、Order 已是 `PAID`。第二次回调不创建第二笔 Payment，不重复扣 Stock。

## 与 minimart-lake

湖仓第一期按一单一 SKU、Kafka `orders.events` 来写，且 Kafka 本身未定。本仓库：

- **不**把 Order 建成一单一 SKU 来迁就那份示意 JSON。
- **不**在 v1 契约里要求必须写出湖。
- 以后若要出站，在模块边界上做映射（一条订单变更带多行，或一行一条、湖仓改主键），见 [ADR-0001](adr/0001-oltp-independent-of-lake.md)。
