package com.github.example;
import com.github.example.net.Http;
import com.github.example.resp.Region;
import java.io.IOException;
import java.net.URL;


public class Application {

    public static void main(String[] args) {
        http();
    }


    public static void http()  {
        try {
            String uri = "https://cdn.jsdelivr.net/gh/pkg6/region-data@main/simple_china_region_level4.json";
            URL url = new URL(uri);
            StringBuilder stringBuilder = Http.get(url);
            Region[] regions = Region.parseJson(stringBuilder.toString());
            for (Region region : regions) {
                System.out.println("id:"+region.getId()+" name:"+region.getName() + " pid:"+ region.getPid());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
