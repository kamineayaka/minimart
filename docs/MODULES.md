# 模块划分

v1 是 **一个可部署进程里的多个模块**，不是一群已拆开的服务。模块名即将来服务名；现在用来锁所有权，避免表和规则互相穿透。决策见 [ADR-0002](adr/0002-modular-monolith-first.md)。

用语见 [CONTEXT.md](../CONTEXT.md)，行为见 [V1-CONTRACT.md](V1-CONTRACT.md)。

本文只划边界，不规定包名、仓库目录或 Gradle 工程怎么建。

## 总图

```
identity          catalog
    │                 │
    │                 ├── stock 预占 / 扣减 / 释放
    ▼                 ▼
   User / Address    Category / SPU / SKU / Stock
    │                 │
    └────────┬────────┘
             ▼
           cart  ──结算──►  order  ◄──成功──  payment
             │                │
             │                └── 禁止支付模块直接改 Stock
             └── 禁止把 CartLine 改成 OrderLine
```

## 模块

### identity

**拥有**：User、Address、登录身份。

**不拥有**：Cart、Order、SKU。

其他模块只引用 User 的标识，以及下单时读取并 **抄写** Address。不得在 identity 里下单或改库存。

### catalog

**拥有**：Category、SPU、SKU、Stock，以及售价。

**不拥有**：Cart、Order、Payment。

对外提供：按 SKU 读可售信息；按数量 **预占 / 确认扣减 / 释放** Stock。只有 catalog 能改 Stock。order 通过这些能力用库存，禁止直接改库存数据。

SPU 负责展示与归类；能卖的是 SKU。

### cart

**拥有**：Cart、CartLine。

**不拥有**：Order、Payment、Stock。

加购时向 catalog **读** SKU 是否可售、当前价；Cart 里可以记当时看到的价，但结算必须以 catalog 当时售价再快照到 OrderLine（契约第 3 条）。

结算不是 cart 的内部改写：由 order 创建 Order，成功后 cart 丢掉对应行。

### order

**拥有**：Order、OrderLine、Order 状态机。

**不拥有**：Payment 记录、Stock、User 档案、Cart 存储。

结算路径：校验 User 与 Address → 向 catalog 预占各行 SKU → 创建 Order（`PENDING_PAY`）→ 通知 cart 清理。取消/超时：状态到 `CANCELLED`，向 catalog 释放预占。看到 Payment 成功：状态到 `PAID`，向 catalog 确认扣减。整单退：状态到 `REFUNDED`，向 catalog 归还数量。

禁止：没有成功 Payment 就把 Order 标成 `PAID`；禁止在 OrderLine 上另搞一套库存。

### payment

**拥有**：Payment 及其状态。

**不拥有**：Order 行、Stock、Cart。

针对一笔已存在的 `PENDING_PAY` Order 发起收钱（v1 渠道可以是假的，但记录必须是真的）。成功后 **通知 order**，由 order 迁状态并确认库存。失败只更新 Payment，不改 Stock。

禁止 payment 直接改 Order 行或 SKU 库存。

### 共享内核（不是业务模块）

跨模块共用的只有标识与金额这类无行为的值（例如 User 标识、SKU 标识、Order 标识、以分为单位的金额）。

共享内核 **没有** 自己的表，也不实现下单或支付。禁止把业务规则堆进「common / util」。

## 允许的依赖方向

| 谁 | 可以依赖谁 | 目的 |
|----|------------|------|
| cart | identity、catalog | 知道是谁的车、SKU 能否加 |
| order | identity、catalog、cart | 抄地址、预占库存、结算后清车 |
| payment | order（只到「针对哪一笔 Order」） | 收哪一单的钱 |
| identity | （无其他业务模块） | |
| catalog | （无其他业务模块） | |

不允许：catalog 依赖 order；payment 依赖 catalog；cart 依赖 order 或 payment。

模块之间 **不互相写对方拥有的数据**。需要对方改变状态时，走该模块公开的能力（预占库存、标记支付成功、清空购物车），而不是改对方的存储。

## 以后拆 Spring Cloud 时

保持上述所有权与依赖方向。一个模块变成一个进程时，把「公开的能力」换成接口即可。v1 **不要**为每个模块单独准备一个库——Stock 与 SKU 同在 catalog，拆库是以后的事。

出站给湖仓（若做）挂在 order 状态变化之后，作为适配，不单列为与 identity / catalog 平级的业务模块。见 [ADR-0001](adr/0001-oltp-independent-of-lake.md)。
