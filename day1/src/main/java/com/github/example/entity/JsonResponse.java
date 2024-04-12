package com.github.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;


@Data
//@NoArgsConstructor
@AllArgsConstructor
public class JsonResponse<T> implements Serializable {
    private Integer code;
    private String Message;
    private T data;
    public JsonResponse(Integer code, String message) {
        this.code = code;
        this.Message = message;
    }
    public static <T> JsonResponse<T> success(T data) {
        return new JsonResponse<>(200, "Success", data);
    }

    public static <T> JsonResponse<T> error(int code, String message) {
        return new JsonResponse<>(code, message, null);
    }
}
