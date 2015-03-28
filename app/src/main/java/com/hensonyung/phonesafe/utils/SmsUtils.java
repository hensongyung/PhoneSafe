package com.hensonyung.phonesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 短信的工具类
 */
public class SmsUtils {
    public static void backupSms(Context context) throws Exception {
        ContentResolver resolver = context.getContentResolver();
        File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");
        FileOutputStream fos = new FileOutputStream(file);

        XmlSerializer serializer = Xml.newSerializer(); //获取xml文件的生成器（序列化器——内存的数据写到文件中）
        //初始化
        serializer.setOutput(fos,"utf-8");

        serializer.startDocument("utf-8",true);
        serializer.startTag(null, "smss");
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor =resolver.query(uri, new String[]{"body", "address", "type", "date"}, null, null, null);
        while (cursor.moveToNext()){
            String body = cursor.getString(0);
            String address = cursor.getString(1);
            String type=cursor.getString(2);
            String date = cursor.getString(3);
            serializer.startTag(null,"sms");

            serializer.startTag(null,"body");
            serializer.text(body);
            serializer.endTag(null,"body");

            serializer.startTag(null,"address");
            serializer.text(address);
            serializer.endTag(null,"address");

            serializer.startTag(null,"type");
            serializer.text(type);
            serializer.endTag(null,"type");

            serializer.startTag(null,"date");
            serializer.text(date);
            serializer.endTag(null,"date");

            serializer.endTag(null,"sms");
        }
        cursor.close();
        serializer.endTag(null,"smss");
        serializer.endDocument();
        fos.close();
    }
}
