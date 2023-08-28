package com.imapcloud.nest.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;

/**
 * @author wmin
 */
public class AseUtils {
    private static final String AES_KEY = "#imapcloud#geoai";
    private static final String CONNECTOR = "@#&";

    public static void main(String[] args) throws Exception {
        String zkytShowToken = createToken("zhaoqing", "1234abc#");
        System.out.println(zkytShowToken);
        String[] strings1 = parseToken(zkytShowToken);
//        System.out.println(strings1[0] + strings1[1]);
    }

    public static String createToken(String account, String password) throws Exception {
        String message = account + CONNECTOR + password;
        // 128位密钥 = 16 bytes Key:
        byte[] key = AES_KEY.getBytes("UTF-8");
        // 加密:
        byte[] data = message.getBytes("UTF-8");
        byte[] encrypted = encrypt(key, data);

        return java.util.Base64.getEncoder().encodeToString(encrypted);
    }

    public static String[] parseToken(String token) throws Exception {
        byte[] key = AES_KEY.getBytes("UTF-8");
        byte[] decode = Base64.getDecoder().decode(token);
        byte[] decrypted = decrypt(key, decode);
        String pt = new String(decrypted, "UTF-8");
        return pt.split(CONNECTOR);
    }

    private static byte[] encrypt(byte[] key, byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKey keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }

    private static byte[] decrypt(byte[] key, byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKey keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }
}
