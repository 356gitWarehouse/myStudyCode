
package com.example.product.vo;

import com.example.product.entity.Product;


/**
 * 商品视图对象：继承 Product，额外带分类名称（多表关联结果）
 */
public class ProductVO extends Product {
    private String categoryName;

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    @Override
    public String toString() {
        return "ProductVO{" + super.toString() + ", categoryName='" + categoryName + '\'' + '}';
    }




}