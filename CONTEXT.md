# MiniMart

MiniMart 是单店 B2C 电商：购物者浏览 SKU、下单并支付。本文件只定义用语；v1 规则见 [docs/V1-CONTRACT.md](docs/V1-CONTRACT.md)，模块边界见 [docs/MODULES.md](docs/MODULES.md)。

## Language

### 人

**User**:
在本店注册并登录的购物者；Cart 与 Order 都归属于 User。
_Avoid_: Customer、Account、Buyer、Client

**Address**:
User 名下的一个收货地点；下单时把当时的内容抄到 Order 上，之后改地址不影响已下的单。
_Avoid_: 把「收货人」建成与 User 平级的实体

### 目录

**Category**:
商品分类树上的一个节点。
_Avoid_: Type、Tag、Department

**SPU**:
货架上的一件商品（列表页上的那一项），下面可以挂多个 SKU。
_Avoid_: Product、Item、Goods（口语里的「商品」若指可买单元，应说 SKU）

**SKU**:
可出售的最小单元，自带售价与 Stock。加购、下单、锁库存都针对 SKU，不针对 SPU。
_Avoid_: Variant、Spec、Item、Product

**Stock**:
某个 SKU 当前还能卖的数量。
_Avoid_: Inventory、Warehouse（v1 没有仓、没有库存单据）

### 交易

**Cart**:
某个 User 当前尚未结算的选购，不是 Order。
_Avoid_: Basket、把 Cart 行直接改成订单行

**CartLine**:
Cart 里一行：一个 SKU 和数量。
_Avoid_: CartItem（易与 OrderLine 混）、OrderLine

**Order**:
已经提交的一笔购买单：一个头、至少一行 OrderLine，状态会随时间变化。
_Avoid_: Transaction、Ticket、流水；也不要把 lake 文档里「一单一 SKU」的 Order 当成这里的 Order

**OrderLine**:
Order 上的一行：一个 SKU、数量、以及下单当下的单价快照。
_Avoid_: Item、Detail；不要用 SKU 一词指这一行

**Payment**:
针对某一笔 Order 收钱的记录。支付成功才是 Order 变为已支付的正规入口。
_Avoid_: 把 Order 本身叫成支付；Transaction、流水

**Refund**:
对一笔已支付 Order 的整单退回。
_Avoid_: 部分退、按行退
