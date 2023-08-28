package com.imapcloud.nest.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * AI表计读数工具类
 *
 * @author: zhengxd
 * @create: 2021/1/5
 **/
public class AIMeterReadUtil {

    // 表计读数接口地址
    private static final String RES_URL = "http://14.23.115.50:9123/ai/numericalRead";


    /**
     * 请求表计读数接口
     * @param imgData
     * @return
     */
    public static String meterRead(String imgData) {
        String result = null;
        Map param = new HashMap();
        param.put("deviceType", "recHz");
        param.put("dataType", 1);
        param.put("imgData", imgData);
        String json = JSON.toJSONString(param);
        try {
            Connection.Response response = Jsoup.connect(RES_URL)
                    .requestBody(json)
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .method(Connection.Method.POST).execute();
            System.out.println(response.body());
            result = response.body();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
