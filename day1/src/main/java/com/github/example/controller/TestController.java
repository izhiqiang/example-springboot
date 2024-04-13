package com.github.example.controller;

import com.github.example.entity.JsonResponse;
import com.github.example.exception.BusinessException;
import com.github.example.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class TestController {
    @GetMapping("/")
    public List<User> getUsers(){
        List<User> list = new ArrayList<>();
        User test1 = new User();
        test1.setId(1);
        test1.setUserName("username1");
        list.add(test1);
        User test2 = new User();
        test2.setId(2);
        test2.setUserName("username2");
        list.add(test2);
        return list;
    }
    @GetMapping("/save")
    public User user(User user){
        return user;
    }
}
