package com.example.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.product.entity.Product;
import com.example.product.vo.ProductVO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    /** 分页 + 多条件（名称、分类、价格区间），返回带分类名的 VO */
    Page<ProductVO> page(int page, int size, String name, Long categoryId,
                         BigDecimal minPrice, BigDecimal maxPrice);

    /** 查详情，带分类名 */
    ProductVO findVOById(Long id);

    /** 新增，返回新 id */
    Long add(Product product);

    /** 修改 */
    void update(Long id, Product product);

    /** 删除 */
    void remove(Long id);

    /** 不分页的多表查询 */
    List<ProductVO> listWithCategory(String name, Long categoryId);
}