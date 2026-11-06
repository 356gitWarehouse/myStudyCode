package com.example.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController            // 返回字符串/JSON，不经过视图解析
public class HelloController {

    @GetMapping("/api/hello")   // 处理 GET /api/hello
    public String hello() {
        return "hello backend";
    }
}