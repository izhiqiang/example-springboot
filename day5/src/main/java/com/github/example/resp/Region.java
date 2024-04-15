package com.github.example.resp;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class Region {
    private int id;
    private String name;
    private int pid;
    private int level;

    public static Region[] parseJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Region[].class);
    }
}
