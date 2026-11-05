package com.example.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.product.common.BusinessException;
import com.example.product.entity.Product;
import com.example.product.mapper.ProductMapper;
import com.example.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service   // 注册为业务层 Bean，Controller 按 ProductService 接口注入它
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override   // 实现接口里的 page 方法
    public Page<Product> page(int page, int size, String name, Long categoryId) {
        Page<Product> p = new Page<>(page, size);
        LambdaQueryWrapper<Product> w = new LambdaQueryWrapper<>();
        w.like(name != null && !name.isEmpty(), Product::getName, name)
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .orderByDesc(Product::getId);
        return productMapper.selectPage(p, w);
    }

    // ① 查单个
    @Override   // 实现接口里的 findById 方法
    public Product findById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        return product;
    }

    // ② 查列表
    @Override   // 实现接口里的 listAll 方法
    public List<Product> listAll() {
        return productMapper.selectList(
                new LambdaQueryWrapper<Product>().orderByDesc(Product::getId));
    }

    // ③ 新增
    @Override   // 实现接口里的 add 方法
    @Transactional(rollbackFor = Exception.class)   // 方法级事务：出现任何异常都整体回滚
    public Long add(Product product) {
        if (product.getPrice() == null || product.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "商品金额必须大于0");
        }
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new BusinessException(400, "商品名称不能为空");
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

    // ④ 修改
    @Override   // 实现接口里的 update 方法
    @Transactional(rollbackFor = Exception.class)   // 方法级事务：出现任何异常都整体回滚
    public void update(Long id, Product product) {
        Product exist = productMapper.selectById(id);

        if (exist == null) {
            throw new BusinessException(404, "商品不存在");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "商品金额必须大于0");
        }
        product.setId(id);   // 用路径里的 id，防止 body 里传别的 id
        productMapper.updateById(product);
    }

    // ⑤ 删除
    @Override   // 实现接口里的 remove 方法
    @Transactional(rollbackFor = Exception.class)   // 方法级事务：出现任何异常都整体回滚
    public void remove(Long id) {
        Product exist = productMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(404, "商品不存在");
        }
        productMapper.deleteById(id);
    }
}