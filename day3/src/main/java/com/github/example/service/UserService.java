package com.github.example.service;

import com.github.example.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    public User getUser(int userId) {
        log.info("根据ID 获取用户信息从DB读取，说明没有走缓存");
        User user = new User(userId, "nocache"+userId, "password_"+userId);
        return user;
    }
}
