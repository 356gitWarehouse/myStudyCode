package com.example.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.product.common.BusinessException;
import com.example.product.entity.Product;
import com.example.product.mapper.CategoryMapper;
import com.example.product.mapper.ProductMapper;
import com.example.product.service.ProductService;
import com.example.product.vo.ProductVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service   // 注册为业务层 Bean，Controller 按 ProductService 接口注入它
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    public ProductServiceImpl(ProductMapper productMapper, CategoryMapper categoryMapper) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Page<ProductVO> page(int page, int size, String name, Long categoryId,
                                BigDecimal minPrice, BigDecimal maxPrice) {
        // 参数钳制：页码至少 1，每页 1~100 条，防止一次查出整库
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        return productMapper.selectVoPage(new Page<>(safePage, safeSize),
                trimToNull(name), categoryId, minPrice, maxPrice);
    }

    @Override
    public ProductVO findVOById(Long id) {
        ProductVO vo = productMapper.selectVoById(id);
        if (vo == null) {
            throw new BusinessException(404, "商品不存在");
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)   // 方法级事务：出现任何异常都整体回滚
    public Long add(Product product) {
        if (isBlank(product.getName())) {
            throw new BusinessException(400, "商品名称不能为空");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "商品金额必须大于0");
        }
        if (product.getCategoryId() == null) {
            throw new BusinessException(400, "商品分类不能为空");
        }
        if (categoryMapper.selectById(product.getCategoryId()) == null) {
            throw new BusinessException(400, "分类不存在");
        }
        Long count = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getName, product.getName()));
        if (count > 0) {
            throw new BusinessException(400, "商品名称已存在");
        }
        product.setId(null);   // 防止前端传 id
        productMapper.insert(product);
        return product.getId();   // 插入后 MyBatis-Plus 回填 id
    }

    @Override
    @Transactional(rollbackFor = Exception.class)   // 方法级事务：出现任何异常都整体回滚
    public void update(Long id, Product product) {
        if (productMapper.selectById(id) == null) {
            throw new BusinessException(404, "商品不存在");
        }
        // 只校验“传了”的字段，支持只改库存这类局部更新
        if (product.getName() != null && isBlank(product.getName())) {
            throw new BusinessException(400, "商品名称不能为空");
        }
        if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "商品金额必须大于0");
        }
        if (product.getCategoryId() != null
                && categoryMapper.selectById(product.getCategoryId()) == null) {
            throw new BusinessException(400, "分类不存在");
        }
        product.setId(id);   // 用路径里的 id，防止 body 里传别的 id
        productMapper.updateById(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)   // 方法级事务：出现任何异常都整体回滚
    public void remove(Long id) {
        if (productMapper.selectById(id) == null) {
            throw new BusinessException(404, "商品不存在");
        }
        productMapper.deleteById(id);
    }

    @Override
    public List<ProductVO> listWithCategory(String name, Long categoryId) {
        return productMapper.selectWithCategory(trimToNull(name), categoryId);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}