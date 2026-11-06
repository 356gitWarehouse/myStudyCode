package com.example.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@TableName("category")          // 对应数据库的 category 表
public class Category {
    @TableId(type = IdType.AUTO)   // 主键，交给数据库自增
    private Long id;
    @NotBlank(message = "分类名称不能为空")      // 新增时不能为 null / 空串 / 纯空格
    @Size(max = 50, message = "分类名称不能超过50个字符")
    private String name;

    @Min(value = 0, message = "排序值不能为负数")   // 越小越靠前
    private Integer sort;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")   // 返回时间不带 T
    private LocalDateTime createTime;

    public Category() {}

    public Category(Long id, String name, Integer sort, LocalDateTime createTime) {
        this.id = id;
        this.name = name;
        this.sort = sort;
        this.createTime = createTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return "Category{" + "id=" + id + ", name='" + name + '\'' + ", sort=" + sort + '}';
    }
}