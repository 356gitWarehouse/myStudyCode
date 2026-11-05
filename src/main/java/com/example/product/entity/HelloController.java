package com.example.product.entity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController            // 返回字符串/JSON，不经过视图解析
public class HelloController {
    @RequestMapping("/hello")   // 处理 GET /hello
    public String hello() {
        System.out.println("hello world");
        return "hello world";
    }
}