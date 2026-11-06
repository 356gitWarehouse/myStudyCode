package com.example.product.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@TableName("product")            // 指定这个类对应数据库的 product 表
public class Product {
    @TableId(type = IdType.AUTO)   // 主键字段，AUTO 表示交给数据库自增
    private Long id;
    @NotBlank(message = "商品名称不能为空")        // 新增时不能为 null / 空串 / 纯空格
    @Size(max = 100, message = "商品名称不能超过100个字符")
    private String name;

    @NotNull(message = "商品金额不能为空")         // 新增时必填
    @DecimalMin(value = "0.01", message = "商品金额必须大于0")
    private BigDecimal price;

    @Min(value = 0, message = "库存不能为负数")    // 库存可不传，传了就不能为负
    private Integer stock;

    @NotNull(message = "商品分类不能为空")         // 新增时必填
    private Long categoryId;

    @Min(value = 0, message = "状态只能是 0 或 1")  // 0 下架 / 1 上架
    @Max(value = 1, message = "状态只能是 0 或 1")
    private Integer status;

    @Size(max = 255, message = "备注不能超过255个字符")
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")   // 接口输出格式：去掉 LocalDateTime 默认的 T
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public Product(Long id, String name, BigDecimal price, Long categoryId, Integer stock, Integer status, String remark, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
        this.stock = stock;
        this.status = status;
        this.remark = remark;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override   // 重写 toString，打印对象时输出各字段的值
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", categoryId=" + categoryId +
                ", status=" + status +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
