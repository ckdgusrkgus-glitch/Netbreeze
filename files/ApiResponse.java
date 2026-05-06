package com.netbreeze.flathome.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 공통 API 응답 래퍼
 *
 * {
 *   "status": 200,
 *   "message": "ok",
 *   "data": { ... }
 * }
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final int    status;
    private final String message;
    private final T      data;

    private ApiResponse(int status, String message, T data) {
        this.status  = status;
        this.message = message;
        this.data    = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), "ok", data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(HttpStatus.CREATED.value(), "created", data);
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}
