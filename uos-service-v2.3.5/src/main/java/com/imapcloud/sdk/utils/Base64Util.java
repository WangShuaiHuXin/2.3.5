package com.imapcloud.sdk.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Objects;

/**
 * @author wmin
 * base64编码工具
 */
@Slf4j
public class Base64Util {
    /**
     * 编码
     *
     * @param input
     * @return
     */
    public static String encodeBase64(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }


    /**
     * 解码
     *
     * @param input
     * @return
     */
    public static byte[] decodeBase64(byte[] input) {
        return Base64.getDecoder().decode(input);
    }

//    /**
//     * 图片转base64
//     * @deprecated 2.2.3，使用新接口{@link Base64Util#getImageBase64(java.io.InputStream)}替代，将在后续删除
//     */
//    @Deprecated
//    public static String imgToBase64(String imgFilePath) {
//        File file = new File(imgFilePath);
//        if(!file.exists()){
//            return "";
//        }
//        try {
//            FileInputStream fis = new FileInputStream(imgFilePath);
//            byte[] data = new byte[fis.available()];
//            fis.read(data);
//            fis.close();
//            return encodeBase64(data);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    /**
     * 图片转base64
     */
    public static String getImageBase64(InputStream inputSteam) {
        if(Objects.isNull(inputSteam)){
            return "";
        }
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();InputStream is = inputSteam){
            IOUtils.copy(is, bos);
            return encodeBase64(bos.toByteArray());
        } catch (IOException e) {
            log.error("Base64 生成失败");
        }
        return "";
    }

}
