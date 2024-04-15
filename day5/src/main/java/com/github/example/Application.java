package com.github.example;
import com.github.example.resp.Region;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Application {

    public static void main(String[] args)  {
        try {
            String uri = "https://cdn.jsdelivr.net/gh/pkg6/region-data@main/simple_china_region_level4.json";
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000); // 设置连接超时时间为5秒
            con.setReadTimeout(5000); // 设置读取超时时间为5秒
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            Region[] regions = Region.parseJson(content.toString());
            for (Region region : regions) {
                System.out.println("id:"+region.getId()+" name:"+region.getName() + " pid:"+ region.getPid());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
