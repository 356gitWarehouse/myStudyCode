-- 初期考核项目建库建表脚本
-- 执行方式（Navicat 里直接跑，或命令行）：
--   & "D:\DevTools\mysql-8.0.44\bin\mysql.exe" -uroot -pRoot@123456 < "D:\javadocuments\product.xml\sql\init.sql"
--
-- 说明：
--   1. category 与 product 是一对多；product.category_id 建索引并加外键
--   2. 金额用 DECIMAL，不用 DOUBLE（避免浮点误差）
--   3. 时间字段用 DATETIME + CURRENT_TIMESTAMP 默认值，create_time 由数据库生成
--   4. 字符集统一 utf8mb4

CREATE DATABASE IF NOT EXISTS demo_db
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE demo_db;

DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;

CREATE TABLE category
(
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(50) NOT NULL COMMENT '分类名，唯一',
    sort        INT         NOT NULL DEFAULT 0 COMMENT '排序值',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_category_name (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品分类';

CREATE TABLE product
(
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(100)  NOT NULL COMMENT '商品名，唯一',
    price       DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '单价',
    stock       INT           NOT NULL DEFAULT 0 COMMENT '库存',
    category_id BIGINT        NOT NULL COMMENT '所属分类',
    status      TINYINT       NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
    remark      VARCHAR(255)  DEFAULT NULL COMMENT '备注',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_name (name),
    KEY idx_category_id (category_id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品';

-- 示例数据：Postman 的 GET /api/products/1 直接能查到东西
INSERT INTO category (name, sort)
VALUES ('手机数码', 1),
       ('图书', 2);

INSERT INTO product (name, price, stock, category_id, status)
VALUES ('iPhone 15', 5999.00, 50, 1, 1),
       ('深入理解Java虚拟机', 89.90, 200, 2, 1);

-- 执行完自检
SELECT c.name AS 分类, COUNT(p.id) AS 商品数
FROM category c
         LEFT JOIN product p ON p.category_id = c.id
GROUP BY c.id, c.name;
