package com.imapcloud.nest.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherUtils {

    public String getCaiYunData(String lng, String lat, int step, String key){
        String url = "https://api.caiyunapp.com/v2.5/" + key + "/%s,%s/daily.json?dailysteps="+step;
        url = String.format(url, lng, lat);//经度,纬度
//        System.out.println(url);
        return client(url);
    }

    public String client(String url) {
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> responseEntity = template.getForEntity(url,String.class);
        return responseEntity.getBody();
    }

}
