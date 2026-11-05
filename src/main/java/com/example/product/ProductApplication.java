package com.example.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication                       // 启动类：开启自动配置，并扫描本包及子包下的组件
@MapperScan("com.example.product.mapper")    // 扫描 Mapper 接口所在包，省去每个接口写 @Mapper
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}