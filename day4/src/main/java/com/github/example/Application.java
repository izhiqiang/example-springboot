package com.github.example;

import com.github.example.model.User1;
import com.github.example.model.User2;
import org.springframework.beans.BeanUtils;

public class Application {
    public static void main(String[] args) {
        User1 user1 = new User1();
        user1.setId(2);
        user1.setName("name1");
        User2 user2 = new User2();
        BeanUtils.copyProperties(user1,user2);
        System.out.println(user2.getId());
    }
}


