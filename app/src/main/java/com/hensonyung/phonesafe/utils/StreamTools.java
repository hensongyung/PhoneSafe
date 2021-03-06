package com.hensonyung.phonesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wengjiasheng on 2015/3/19.
 */
public class StreamTools {
    public static String readFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) !=-1){
            baos.write(buffer,0,len);
        }
        is.close();
        String result = baos.toString();
        baos.close();
        return result;
    }
}
