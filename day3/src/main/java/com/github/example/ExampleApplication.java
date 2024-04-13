package com.github.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//docker pull redis:7
//docker run --restart=always -p 6379:6379 --name myredis -d redis:7  --requirepass 123456

@SpringBootApplication
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class,args);
    }
}
