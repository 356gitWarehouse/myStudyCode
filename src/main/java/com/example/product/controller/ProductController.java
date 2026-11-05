package com.example.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.product.common.Result;
import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                      // 控制器：方法返回值直接转成 JSON，不找视图页面
@RequestMapping("/api/products")     // 类级路径前缀，下面所有接口都以它开头
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ① 查单个
    @GetMapping("/{id}")                                   // 处理 GET /api/products/{id}
    public Result<Product> get(@PathVariable Long id) {    // 取 URL 路径里的 id
        return Result.success(productService.findById(id));
    }

    // ② 查列表
    @GetMapping                                            // 处理 GET /api/products
    public Result<List<Product>> list() {
        return Result.success(productService.listAll());
    }

    // ③ 新增
    @PostMapping                                           // 处理 POST /api/products
    @ResponseStatus(HttpStatus.CREATED)                    // 新增成功返回 201，而不是 200
    public Result<Long> add(@RequestBody Product product) { // 取请求体里的 JSON
        return Result.success(productService.add(product));
    }

    // ④ 修改
    @PutMapping("/{id}")                                   // 处理 PUT /api/products/{id}
    public Result<Void> update(@PathVariable Long id,      // 路径里的 id
                               @RequestBody Product product) {  // 请求体里要改的字段
        productService.update(id, product);
        return Result.success(null);
    }

    // ⑤ 删除
    @DeleteMapping("/{id}")                                // 处理 DELETE /api/products/{id}
    public Result<Void> remove(@PathVariable Long id) {    // 路径里的 id
        productService.remove(id);
        return Result.success(null);
    }

    // ⑥ 分页 + 条件查询
    @GetMapping("/search")                                 // 处理 GET /api/products/search
    public Result<Page<Product>> page(
            @RequestParam(defaultValue = "1") int page,    // 页码，不传默认第 1 页
            @RequestParam(defaultValue = "10") int size,   // 每页条数，不传默认 10
            @RequestParam(required = false) String name,   // 商品名模糊匹配，可不传
            @RequestParam(required = false) Long categoryId) {  // 分类筛选，可不传
        return Result.success(productService.page(page, size, name, categoryId));
    }
}