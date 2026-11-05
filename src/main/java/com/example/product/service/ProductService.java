package com.example.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.product.entity.Product;
import java.util.List;

public interface ProductService {
    Product findById(Long id);
    List<Product> listAll();
    Long add(Product product);          // 返回新 id
    void update(Long id, Product product);
    void remove(Long id);
    Page<Product> page(int page, int size, String name, Long categoryId);
}
