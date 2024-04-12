package com.github.example.advice;

import com.github.example.entity.JsonResponse;
import com.github.example.exception.BusinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(value = BusinessException.class)
    public JsonResponse<String> handlerException(HttpServletRequest req, BusinessException e) {
        JsonResponse<String> response = new JsonResponse<>(-1, "The server has run away");
        response.setData(e.getMessage());
        return response;
    }
}
