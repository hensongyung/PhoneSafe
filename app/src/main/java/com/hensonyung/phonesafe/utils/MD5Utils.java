package com.hensonyung.phonesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wengjiasheng on 2015/3/22.
 */
public class MD5Utils {
    public static String md5psw(String password){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        byte[]resul = digest.digest(password.getBytes());
        StringBuffer buffer = new StringBuffer();
        for (byte b : resul){
            int number = b & 0xff;
            String str =Integer.toHexString(number);
            if (str.length() == 1){
                buffer.append("0");
            }
            buffer.append(str);
        }
        return buffer.toString();
    }
}
