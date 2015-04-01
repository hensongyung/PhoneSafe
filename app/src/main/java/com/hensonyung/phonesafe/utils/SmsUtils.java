package com.hensonyung.phonesafe.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import com.hensonyung.phonesafe.AtoolsActivity;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 短信的工具类
 */
public class SmsUtils {
    /**
     * 备份短信的回调接口
     */
    public interface BackupCallBack{
        public void beforeBackup(int max);
        public void onSmsBackup(int progress);
    }

    public static void backupSms(Context context,BackupCallBack callBack) throws Exception {
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

        int max = cursor.getCount();
//        pd.setMax(max);
        int process = 0;
        callBack.beforeBackup(max);
        serializer.attribute(null,"max",max +"");
        while (cursor.moveToNext()){
            Thread.sleep(500);
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

            //备份过程增加进度
            process++;
//            pd.setProgress(process);
            callBack.onSmsBackup(process);
        }
        cursor.close();
        serializer.endTag(null,"smss");
        serializer.endDocument();
        fos.close();
    }


    public static void smsRestore(Context context,boolean flag){
        Uri uri = Uri.parse("content://sms/");
        if (flag){
            context.getContentResolver().delete(uri,null,null);
        }
        //1.读取sd卡上的xml文件
        //2.读取max
        //3.读取每一条短信，body，data type address
        //4把短信插入到系统短信应用

        ContentValues values = new ContentValues();
        values.put("body","woshi duanxin de neirong");
        values.put("date","134321435");
        values.put("type","1");
        values.put("address","5556");
        context.getContentResolver().insert(uri,values);

    }
}
