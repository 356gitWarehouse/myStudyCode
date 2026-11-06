package com.example.product.service;

import com.example.product.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> listAll();
    Long add(Category category);
    void remove(Long id);
}