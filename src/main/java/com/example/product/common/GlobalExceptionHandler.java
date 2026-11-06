package com.example.product.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器：把 Controller 抛出的异常统一转成「真实 HTTP 状态码 + 业务 JSON 体」
 *
 * HTTP 状态码 vs 业务 code 的映射：
 *   200 OK            → 成功（由 Controller 正常返回 ResponseEntity 时设置）
 *   400 BAD_REQUEST   → 参数错误 / 类型不匹配 / 数据约束违反
 *   404 NOT_FOUND     → 资源不存在（URL 找不到 or 业务上查不到数据）
 *   500 INTERNAL_SERVER_ERROR → 兜底未知异常
 */
@RestControllerAdvice   // 全局异常处理器：统一转成「真实 HTTP 状态码 + 业务 JSON」
public class GlobalExceptionHandler {

    // ========== 1. 业务异常：根据 BusinessException 里携带的 code 映射 HTTP 状态码 ==========
    @ExceptionHandler(BusinessException.class)   // 业务异常：400/404/401/403 按 code 映射
    public ResponseEntity<Result<Void>> handleBusiness(BusinessException e) {
        HttpStatus status = switch (e.getCode()) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 404 -> HttpStatus.NOT_FOUND;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 403 -> HttpStatus.FORBIDDEN;
            default  -> HttpStatus.INTERNAL_SERVER_ERROR;  // 其他业务码一律按 500
        };
        return ResponseEntity.status(status).body(Result.fail(e.getCode(), e.getMessage()));
    }

    // ========== 2. 外键/唯一约束等数据库异常 → 400 ==========
    @ExceptionHandler(DataIntegrityViolationException.class)   // 外键/唯一约束等数据库异常 → 400
    public ResponseEntity<Result<Void>> handleData(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail(400, "数据不合法：分类不存在或名称重复"));
    }

    // ========== 3. URL 路径不存在 → 404 ==========
    // Boot 3.x 叫 NoResourceFoundException；Boot 2.x 叫 NoHandlerFoundException
    @ExceptionHandler(NoResourceFoundException.class)   // URL 路径不存在 → 404
    public ResponseEntity<Result<Void>> handleNoResource(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Result.fail(404, "接口不存在：" + e.getResourcePath()));
    }

    // ========== 4. 参数类型不匹配 → 400 ==========
    // 例：URL 要求 /api/products/{id} 且 id 为 Long，用户传了 /api/products/abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)   // 参数类型不匹配 → 400
    public ResponseEntity<Result<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String paramName = e.getName();                     // 参数名，比如 "id"
        String requiredType = e.getRequiredType() != null
                ? e.getRequiredType().getSimpleName()
                : "unknown";
        Object value = e.getValue();                        // 用户传的值，比如 "abc"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail(400, String.format(
                        "参数 '%s' 类型错误：期望 %s，实际收到 '%s'", paramName, requiredType, value)));
    }

    // ========== 5. 实体字段校验失败（@Valid）→ 400 ==========
    @ExceptionHandler(MethodArgumentNotValidException.class)   // @Valid 校验请求体失败
    public ResponseEntity<Result<Void>> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getDefaultMessage())
                .findFirst()
                .orElse("参数校验失败");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(400, msg));
    }

    // ========== 6. 方法参数校验失败（@RequestParam 上的 @Min/@Max）→ 400 ==========
    @ExceptionHandler(ConstraintViolationException.class)      // @Validated 参数校验失败
    public ResponseEntity<Result<Void>> handleConstraint(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .findFirst()
                .orElse("参数校验失败");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(400, msg));
    }

    // ========== 7. 请求体不是合法 JSON → 400 ==========
    @ExceptionHandler(HttpMessageNotReadableException.class)   // 请求体不是合法 JSON → 400
    public ResponseEntity<Result<Void>> handleBadBody(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail(400, "请求体格式不正确，请检查 JSON"));
    }

    // ========== 8. 兜底：其他没预料的异常 → 500 ==========
    @ExceptionHandler(Exception.class)   // 兜底：其他未预料异常 → 500
    public ResponseEntity<Result<Void>> handleOther(Exception e) {
        e.printStackTrace();   // 堆栈只打在服务端控制台，不暴露给前端
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.fail(500, "服务器异常，请稍后再试"));
    }
}