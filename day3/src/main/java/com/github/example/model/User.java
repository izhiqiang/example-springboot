package com.github.example.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private int id;

    private String userName;

    private String userPassword;

    public User(int userId, String userName, String userPassword) {
        this.id = userId;
        this.userName = userName;
        this.userPassword = userPassword;
    }
}
