package com.example.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.product.entity.Product;
import com.example.product.vo.ProductVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 多表关联 + 分页查询：商品 LEFT JOIN 分类
     * 第一个参数必须是 Page，MyBatis-Plus 才会自动拼接 LIMIT 并回填 total。
     */
    Page<ProductVO> selectVoPage(Page<ProductVO> page,
                                 @Param("name") String name,             // 商品名模糊
                                 @Param("categoryId") Long categoryId,   // 分类筛选
                                 @Param("minPrice") BigDecimal minPrice, // 最低价
                                 @Param("maxPrice") BigDecimal maxPrice); // 最高价

    /**
     * 按 id 查详情，带分类名。
     */
    ProductVO selectVoById(@Param("id") Long id);

    /**
     * 不分页的多表查询，用于 /api/products/with-category。
     */
    List<ProductVO> selectWithCategory(@Param("name") String name,             // 商品名模糊
                                       @Param("categoryId") Long categoryId);  // 分类筛选
}