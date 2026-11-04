package com.example.product.service.impl;

import com.example.product.entity.Product;
import com.example.product.mapper.ProductMapper;
import com.example.product.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    // 构造方法注入：Spring 自动把 Mapper 传进来
    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public Product findById(Long id) {
        return productMapper.selectById(id);
    }
}
