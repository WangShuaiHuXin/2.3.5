package com.imapcloud.nest.media;

import com.imapcloud.nest.utils.TxStreamUtil;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class StreamGenerateTest {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap(8);
        //此处填对应的名称和id
        map.put("赣州蓉江G900（2号）"+"(360天)", "e367c1ea-d6b0-4318-a2e0-b33dd7aa2bb2");
        for (String s : map.keySet()) {
            String full = map.get(s);
            System.out.println("名称：" + s);
            Map result = new LinkedHashMap(16);
            //无人机
//            Map oavMap = TxStreamUtil.getDefault(full.substring(0, full.indexOf("-")));
//            result.put("无人机推流", oavMap.get("pushRtmp"));
//            result.put("无人机http拉流", oavMap.get("pullHttp"));
//            result.put("无人机rtmp拉流", oavMap.get("pullRtmp"));

            //巢内
//            Map inMap = TxStreamUtil.getInDefault(full);
//            Map inMap = TxStreamUtil.getInDefault(full.substring(0, full.indexOf("-")));
//            result.put("巢内推流",inMap.get("pushInRtmp"));
//            result.put("巢内http拉流",inMap.get("pullInHttp"));
//            result.put("巢内rtmp拉流",inMap.get("pullInRtmp"));
            //巢外
//            Map outMap = TxStreamUtil.getOutDefault(full);
            Map outMap = TxStreamUtil.getOutDefault(full.substring(0, full.indexOf("-")));
            result.put("巢外推流", outMap.get("pushOutRtmp"));
            result.put("巢外http拉流", outMap.get("pullOutHttp"));
            result.put("巢外rtmp拉流", outMap.get("pullOutRtmp"));
            for (Object o : result.keySet()) {
                System.out.println(o + ":" + result.get(o));
            }
        }


    }
}