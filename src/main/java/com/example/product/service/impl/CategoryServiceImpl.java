package com.example.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.product.common.BusinessException;
import com.example.product.entity.Category;
import com.example.product.entity.Product;
import com.example.product.mapper.CategoryMapper;
import com.example.product.mapper.ProductMapper;
import com.example.product.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service   // 注册为业务层 Bean
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper, ProductMapper productMapper) {
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
    }

    @Override
    public List<Category> listAll() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .orderByAsc(Category::getSort)
                        .orderByAsc(Category::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)   // 新增失败（如重名）整体回滚
    public Long add(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new BusinessException(400, "分类名称不能为空");
        }
        Long count = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getName, category.getName().trim()));
        if (count > 0) {
            throw new BusinessException(400, "分类名称已存在");
        }
        category.setId(null);
        category.setName(category.getName().trim());
        if (category.getSort() == null) {
            category.setSort(0);
        }
        categoryMapper.insert(category);
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)   // 删除失败整体回滚
    public void remove(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException(404, "分类不存在");
        }
        // 删除保护：分类下还有商品时不允许删，否则会破坏外键/丢数据
        Long productCount = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, id));
        if (productCount > 0) {
            throw new BusinessException(400, "该分类下还有 " + productCount + " 个商品，不能删除");
        }
        categoryMapper.deleteById(id);
    }
}