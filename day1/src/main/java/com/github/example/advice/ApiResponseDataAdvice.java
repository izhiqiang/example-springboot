package com.github.example.advice;

import com.github.example.annotation.IgnoreApiResponse;
import com.github.example.entity.JsonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ApiResponseDataAdvice implements ResponseBodyAdvice<Object> {
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        // 拿到类声明,判断是否被IgnoreResponseAdvice进行标识
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreApiResponse.class)) {
            return false;
        }
        if (methodParameter.getMethod().isAnnotationPresent(IgnoreApiResponse.class)) {
            return false;
        }
        return true;
    }

    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        JsonResponse<Object> response = new JsonResponse<>(0, "");
        if (o == null) {
            return response;
        } else if (o instanceof JsonResponse) {
            response = (JsonResponse<Object>) o;
        } else {
            response.setData(o);
        }
        return response;
    }
}
