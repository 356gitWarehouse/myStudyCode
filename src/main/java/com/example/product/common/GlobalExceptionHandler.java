package com.example.product.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice   // 全局异常处理器：Controller 抛出的异常都在这里转成统一 JSON
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)   // 只处理业务异常，按它携带的 code 返回
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)   // 兜底：其他没预料的异常统一按 500 返回
    public Result<Void> handleOther(Exception e) {
        e.printStackTrace();          // 堆栈只打在自己控制台
        return Result.fail(500, "服务器异常，请稍后再试");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)   // 外键/唯一约束等数据库异常 → 400
    public Result<Void> handleData(DataIntegrityViolationException e) {
        return Result.fail(400, "数据不合法：分类不存在或名称重复");
    }
}