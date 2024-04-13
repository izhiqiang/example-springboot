package com.github.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {
    private Integer id;
    @JsonProperty(value = "user_name")
    private String UserName;
    //http://127.0.0.1:8080/save?id=1&user_name=dasdjaksl
    // 在结构体中没有user_name属性 所以最简单在麻烦的就需要添加set方法
    public void setUser_name(String name){
        this.UserName = name;
    }
}
