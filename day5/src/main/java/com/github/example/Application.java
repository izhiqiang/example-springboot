package com.github.example;

import com.github.example.net.Http;
import com.github.example.okhttp.OkHttp;
import com.github.example.resp.Region;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class Application {

    public static void main(String[] args) {

        okget();
    }

    public static void okget() {
        OkHttp okHttp = OkHttp.build();
        Map<String, Object> body = new LinkedHashMap<String, Object>();
//            File file = new File("test.txt");
//            body.put("file",file);
        body.put("name", "22");
        try {
            Response response = okHttp.postJSON("https://www.baidu.com/", body);
            System.out.println(response.body().string());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void http() {
        try {
            String uri = "https://cdn.jsdelivr.net/gh/pkg6/region-data@main/simple_china_region_level4.json";
            URL url = new URL(uri);
            StringBuilder stringBuilder = Http.get(url);
            Region[] regions = Region.parseJson(stringBuilder.toString());
            for (Region region : regions) {
                System.out.println("id:" + region.getId() + " name:" + region.getName() + " pid:" + region.getPid());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
