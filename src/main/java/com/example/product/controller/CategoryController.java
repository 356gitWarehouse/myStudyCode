package com.example.product.controller;

import com.example.product.common.Result;
import com.example.product.entity.Category;
import com.example.product.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                       // 返回 JSON
@RequestMapping("/api/categories")    // 分类资源的统一前缀
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 分类列表
    @GetMapping                       // GET /api/categories
    public Result<List<Category>> list() {
        return Result.success(categoryService.listAll());
    }

    // 新增分类
    @PostMapping                      // POST /api/categories
    @ResponseStatus(HttpStatus.CREATED)   // 新增成功返回 201
    public Result<Long> add(@Valid @RequestBody Category category) {   // @Valid 校验名称与排序
        return Result.success(categoryService.add(category));
    }

    // 删除分类：分类下有商品时返回 400
    @DeleteMapping("/{id}")           // DELETE /api/categories/{id}
    public Result<Void> remove(@PathVariable Long id) {
        categoryService.remove(id);
        return Result.success(null);
    }
}