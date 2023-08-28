package com.imapcloud.nest.common.easy;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 直播平台工具类
 * 访问所有方法都需要携带token
 *
 * @author wmin
 */
public class EasyDss {
    private static final String BASE_URL = "http://139.159.221.121:10080";


    /***
     * 登录获取token
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public String login(String username, String password) {
        String contentBase = "username=%s&password=%s";
        String content = String.format(contentBase, username, encrypt(password));
        String url = BASE_URL + "/login";
        String result = getJsonStrFromEasyDssServer(url, content);
        if (result == null) {
            return null;
        }
        JSONObject tokenObject = JSONObject.parseObject(result);
        return tokenObject.getString("token");
    }


    /**
     * 获取直播列表
     */
    public String listLive(Integer start, Integer limit, String token) {
        String contentBase = "start=%s&limit=%s&token=%s";
        String content = String.format(contentBase, start, limit, token);
        String url = BASE_URL + "/live/list";
        String result = getJsonStrFromEasyDssServer(url, content);
        //todo 对返回的JSON字符串进行转换成为java对象

        return result;
    }

    /**
     * 直播流开关
     *
     * @param id      直播流Id
     * @param actived true-> 打开，false -> 关闭
     * @return
     */
    public Boolean switchLive(String id, Boolean actived, String token) {
        String contentBase = "id=%s&actived=%s&token=%s";
        String content = String.format(contentBase, id, actived, token);
        String url = BASE_URL + "/live/turn/actived";
        String result = getJsonStrFromEasyDssServer(url, content);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return "Success".equals(jsonObject.getString("msg"));
    }


    private String getJsonStrFromEasyDssServer(String urlStr, String content) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(content);
            out.flush();
            out.close();
            conn.connect();
            StringBuffer sbf = new StringBuffer();
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * todo 加密方法是否是正确的带校验
     *
     * @param str
     * @return
     */
    private String encrypt(String str) {
        if (str != null) {
            return DigestUtils.md5DigestAsHex(str.getBytes());
        }
        return null;
    }

}
