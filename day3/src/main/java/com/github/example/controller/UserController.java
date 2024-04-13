package com.github.example.controller;

import com.github.example.model.User;
import com.github.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Slf4j
public class UserController {

    
    @Resource
    private UserService userService;



    //查询数据并且添加到缓存
    @GetMapping("/user")
    @Cacheable("userCache")
    public User getUser(@RequestParam(required = true) String id){
        // 添加缓存
        return userService.getUser(Integer.parseInt(id));
    }

    //返回结果userPassword中含有nocache字符串就不缓存
    @GetMapping("/user2")
    @CachePut(value = "userCache", unless = "#result.userPassword.contains('nocache')")
    public User getUser2(@RequestParam(required = true) String id) {
        System.out.println("如果走到这里说明，说明缓存没有生效！");
        User user = new User(Integer.parseInt(id), "name_nocache" + id, "nocache");
        return user;
    }

    //删除一个缓存
    @DeleteMapping("user")
    @CacheEvict("userCache")
    public String deleteUser(@RequestParam(required = true) String id){
        return "删除成功";
    }

    //添加数据到缓存，缓存的key是当前user的id
    @PostMapping("/user")
    @CachePut(value = "userCache", key = "#result.id +''")
    public User saveUser(User user) {
        return user;
    }
}
