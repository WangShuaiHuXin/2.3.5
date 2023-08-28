package com.imapcloud.nest.utils;

import org.springframework.util.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author wmin
 */
public class Md5Util {


    public String encrypt(String str) {
        if (str != null) {
            return DigestUtils.md5DigestAsHex(str.getBytes());
        }
        return null;
    }


}
