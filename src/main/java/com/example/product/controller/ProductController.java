package com.example.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.product.common.Result;
import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import com.example.product.vo.ProductVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController                      // 控制器：方法返回值直接转成 JSON，不找视图页面
@Validated                           // 开启方法参数上的 @Min/@Max 校验
@RequestMapping("/api/products")     // 类级路径前缀，下面所有接口都以它开头
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ① 查单个（带分类名）
    @GetMapping("/{id}")                                    // 处理 GET /api/products/{id}
    public Result<ProductVO> get(@PathVariable Long id) {   // 取 URL 路径里的 id
        return Result.success(productService.findVOById(id));
    }

    // ② 分页 + 多条件查询
    // /api/products 和 /api/products/search 指向同一个方法，兼容旧文档
    @GetMapping({"", "/search"})   // GET /api/products 与 /api/products/search 共用此方法
    public Result<Page<ProductVO>> page(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int page,   // 页码
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页条数必须大于0")
            @Max(value = 100, message = "每页最多100条") int size,                        // 每页条数
            @RequestParam(required = false) String name,      // 商品名模糊匹配，可不传
            @RequestParam(required = false) Long categoryId,  // 分类筛选，可不传
            @RequestParam(required = false) BigDecimal minPrice,  // 最低价，可不传
            @RequestParam(required = false) BigDecimal maxPrice) { // 最高价，可不传
        return Result.success(productService.page(page, size, name, categoryId, minPrice, maxPrice));
    }

    // ③ 新增
    @PostMapping                                           // 处理 POST /api/products
    @ResponseStatus(HttpStatus.CREATED)                    // 新增成功返回 201，而不是 200
    public Result<Long> add(@Valid @RequestBody Product product) { // @Valid 触发实体字段校验
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

    // ⑥ 不分页的多表查询（方便演示 JOIN 结果）
    @GetMapping("/with-category")                          // 处理 GET /api/products/with-category
    public Result<List<ProductVO>> listWithCategory(
            @RequestParam(required = false) String name,          // 商品名模糊匹配
            @RequestParam(required = false) Long categoryId) {    // 按分类筛选
        return Result.success(productService.listWithCategory(name, categoryId));
    }
}