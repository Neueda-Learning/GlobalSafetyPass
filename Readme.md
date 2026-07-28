# Global Safety Pass (Travel Lion)

基于 Spring Boot + MySQL 的旅行助手应用，实现 PDF 中全部三个阶段的核心功能。

## 功能概览

### Phase 1: 出行前 - 行程设置与旅行准备度
- 添加目的地、旅行日期、预算、货币和首选卡片
- 检查卡片有效期、海外支付状态、限额和余额
- 生成准备度评分（0-100）
- 显示警告和建议操作

### Phase 2: 旅行中 - 支付追踪与失败支持
- 记录成功支付到旅行支出
- 追踪花费和剩余预算
- 显示商户、货币、汇率和类别
- 解释支付失败原因并推荐解决方案
- 卡片控制操作（开通海外支付、提高限额）

### Phase 3: 持续监控 - 可疑交易监控
- 检测行程日期/目的地外的交易
- 识别重复扣款和高额取款
- 显示原因和风险等级
- 支持确认、报告欺诈或冻结卡片

## 技术栈

- **后端**: Spring Boot 3.2, Spring Data JPA
- **数据库**: MySQL (root / qianqian825)
- **前端**: HTML/CSS/JS，390px 手机尺寸模拟

## 启动方式

### 前置条件
- JDK 17+
- Maven 3.8+
- MySQL 8.0+ 运行中

### 运行
```bash
cd GlobalSafetyPass
mvn spring-boot:run
```

应用启动后访问: http://localhost:8080

数据库 `travel_assistant` 会自动创建，并插入 4 张示例卡片。

## API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/cards | 获取所有卡片 |
| GET | /api/trips | 获取所有行程 |
| POST | /api/trips | 创建行程并检查准备度 |
| GET | /api/trips/{id}/readiness | 获取准备度评分 |
| GET | /api/trips/{id}/dashboard | 获取旅行面板数据 |
| POST | /api/trips/{id}/transactions | 处理支付 |
| GET | /api/trips/{id}/transactions | 获取交易记录 |
| GET | /api/trips/{id}/alerts | 获取安全警报 |
| POST | /api/alerts/{id}/confirm | 确认交易正常 |
| POST | /api/alerts/{id}/report | 报告欺诈 |
| POST | /api/alerts/{id}/freeze | 冻结卡片 |
| PUT | /api/cards/{id}/controls | 更新卡片设置 |

## 测试场景

1. **无效日期**: 设置结束日期早于开始日期
2. **过期卡片**: 选择 "Expired Card"
3. **海外支付未开通**: 选择 "Debit Card" 并尝试支付
4. **余额不足**: 使用 Debit Card 支付大额
5. **冻结卡片**: 选择 "Frozen Card"
6. **超限支付**: 支付金额超过日限额
7. **可疑交易**: 在错误地点交易、重复扣款、高额ATM取款



1111111111111
