# 商品管理后台 API（product-admin）

Java 后端初期考核项目。基于 **Spring Boot 3.3 + MyBatis-Plus 3.5 + MySQL 8**，实现商品/分类的 RESTful 接口、分页多条件查询、多表关联、统一返回与全局异常处理。

## 技术栈

| 层 | 选型 |
|---|---|
| 语言 / JDK | Java 17 |
| 框架 | Spring Boot 3.3.5 |
| 持久层 | MyBatis-Plus 3.5.5（`BaseMapper` + XML 自定义 SQL） |
| 数据库 | MySQL 8.0 |
| 构建 | Maven 3.9 |
| 接口文档/调试 | Postman 集合（`postman/`） |

## 功能清单

- 商品 CRUD：`GET/POST/PUT/DELETE /api/products`
- 分页 + 多条件：页码、每页条数、名称模糊、分类、价格区间
- 多表关联：商品列表/详情通过 `LEFT JOIN category` 返回 `categoryName`
- 分类管理：列表、新增、删除；**分类下还有商品时拒绝删除（400）**
- 统一返回：`{code, message, data}`，错误同时返回真实 HTTP 状态码（400/404/500）
- 全局异常：业务异常、参数类型错误、未知路径、非法 JSON、数据库约束、兜底异常
- 日期格式：`yyyy-MM-dd HH:mm:ss`（`@JsonFormat`）
- 校验：Bean Validation 注解（`@NotBlank`、`@NotNull`、`@DecimalMin`、`@Min/@Max`、`@Size`）+ `@Valid`/`@Validated`，非法参数统一返回 400
- 事务：新增/修改/删除加 `@Transactional(rollbackFor = Exception.class)`
- 分页参数校验：`page >= 1`、`1 <= size <= 100`，非法直接 400（服务层另有钳制兜底）

## 目录结构

```text
product-admin
├─ pom.xml
├─ sql/init.sql                    建库建表 + 初始数据
├─ postman/                        Postman 集合与环境
└─ src/main
   ├─ java/com/example/product
   │  ├─ ProductApplication.java    启动类
   │  ├─ common/                    Result、BusinessException、全局异常
   │  ├─ config/                    MyBatis-Plus 分页插件
   │  ├─ controller/                ProductController、CategoryController、HelloController
   │  ├─ entity/                    Product、Category
   │  ├─ mapper/                    ProductMapper、CategoryMapper
   │  ├─ service/                   接口 + impl 实现
   │  └─ vo/                        ProductVO（带 categoryName）
   └─ resources
      ├─ application.yml
      └─ mapper/ProductMapper.xml   多表关联 + 分页 SQL
```

## 快速开始

1. **建库**：确认本机 MySQL 8 已启动，执行：

   ```bash
   mysql -uroot -p < sql/init.sql
   ```

   脚本会创建 `demo_db`、`category`、`product` 两张表并插入 2 条示例数据；脚本可重复执行（先 `DROP TABLE IF EXISTS`）。

2. **改配置**：按自己的账号修改 `src/main/resources/application.yml` 里的 `username/password`。

3. **启动**（二选一）：

   ```bash
   mvn spring-boot:run
   # 或
   mvn clean package -DskipTests
   java -jar target/product-admin-1.0.0.jar
   ```

4. **验证**：浏览器/curl 访问 `http://localhost:8080/api/products?page=1&size=5`。

## 接口清单

| 方法 | 路径 | 说明 | 成功状态码 |
|---|---|---|---|
| GET | `/api/products?page=1&size=10&name=&categoryId=&minPrice=&maxPrice=` | 分页 + 多条件，返回 `records/total/size/current` | 200 |
| GET | `/api/products/{id}` | 商品详情（含 `categoryName`） | 200 |
| POST | `/api/products` | 新增商品，返回新 id | 201 |
| PUT | `/api/products/{id}` | 修改商品（支持只传要改的字段） | 200 |
| DELETE | `/api/products/{id}` | 删除商品 | 200 |
| GET | `/api/products/with-category?name=&categoryId=` | 不分页的多表查询（演示用） | 200 |
| GET | `/api/categories` | 分类列表 | 200 |
| POST | `/api/categories` | 新增分类，返回新 id | 201 |
| DELETE | `/api/categories/{id}` | 删除分类；分类下有商品时返回 400 | 200 / 400 |
| GET | `/api/hello` | 连通性测试，返回 `hello backend` | 200 |

错误示例：

```json
// GET /api/products/999999  → HTTP 404
{"code":404,"message":"商品不存在","data":null}

// DELETE /api/categories/1（分类下有商品）→ HTTP 400
{"code":400,"message":"该分类下还有 4 个商品，不能删除","data":null}
```

## 统一返回结构

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

`code` 与 HTTP 状态码保持一致：400 参数/业务校验失败、404 资源不存在、500 服务器异常。

## Postman

1. 导入 `postman/商品管理.postman_collection.json` 与 `postman/backend-local.postman_environment.json`；
2. 选择环境 `backend-local`（`baseUrl=http://localhost:8080`）；
3. 按集合顺序执行：列表 → 单个 → 新增（自动写入 `productId`）→ 修改 → 删除 → 404 → 分类列表/新增/删除。

集合里的断言覆盖：状态码、统一结构、`categoryName`、404、分类删除保护。

## 数据库设计

- `category(id, name, sort, create_time)`
- `product(id, name, price, stock, category_id, status, remark, create_time, update_time)`
  - `uk_product_name` 名称唯一
  - `fk_product_category` 外键指向 `category(id)`
  - `update_time` 由数据库 `ON UPDATE CURRENT_TIMESTAMP` 维护

## 已知限制

- 未做登录/鉴权，接口默认本机可访问；
- 未编写单元测试，验收以 Postman 集合为准；
- `application.yml` 中数据库密码为明文示例，公开仓库前请改为环境变量；
- 删除分类只做“有商品则拦截”，不做级联删除；

## 提交记录

- Day 1–3：建库脚本、实体、单表查询
- Day 4：五个 CRUD 接口、统一返回、全局异常
- Day 5–6：分页多条件、多表关联、分类接口、真实状态码、Postman 集合