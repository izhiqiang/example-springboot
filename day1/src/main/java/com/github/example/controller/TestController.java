package com.github.example.controller;

import com.github.example.annotation.IgnoreApiResponse;
import com.github.example.entity.JsonResponse;
import com.github.example.exception.BusinessException;
import com.github.example.model.Test;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class TestController {
    @GetMapping("/")
    public <T> JsonResponse<T> getAdPlans() throws Exception{
        List<Test> list = new ArrayList<>();
        Test test1 = new Test();
        test1.setId(1);
        list.add(test1);
        Test test2 = new Test();
        test2.setId(2);
        list.add(test2);
        throw new BusinessException("");
//        return JsonResponse.error(201,"你好");
    }
}
