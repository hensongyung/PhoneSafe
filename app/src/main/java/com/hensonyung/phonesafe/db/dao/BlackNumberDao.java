package com.hensonyung.phonesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hensonyung.phonesafe.db.BlackNumverDBOpenHelper;
import com.hensonyung.phonesafe.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wengjiasheng on 2015/3/27.
 */
public class BlackNumberDao {
    private BlackNumverDBOpenHelper helper;

    public BlackNumberDao(Context context) {
        helper = new BlackNumverDBOpenHelper(context);
    }

    //查询黑名单号码是否存在
    public boolean find(String number){
        boolean result = false;
        SQLiteDatabase db =helper.getReadableDatabase();
        Cursor cursor =db.rawQuery("select * from blacknumber where number = ?", new String[]{number});
        if (cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    //查询黑名单模式
    public String findMode(String number){
        String result = null;
        SQLiteDatabase db =helper.getReadableDatabase();
        Cursor cursor =db.rawQuery("select mode from blacknumber where number = ?", new String[]{number});
        if (cursor.moveToNext()){
            result = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return result;
    }

    //添加黑名单号码，拦截模式
    public void add(String number,String mode){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number",number);
        values.put("mode",mode);
        db.insert("blacknumber",null,values);
        db.close();
    }

    //修改黑名单号码
    public void update(String number,String newMode){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode",newMode);
        db.update("blacknumber",values,"number=?",new String[]{number});
        db.close();
    }

    //删除黑名单
    public void delete(String number){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("blacknumber", "number=?", new String[]{number});
        db.close();
    }

    //查询全部
    public List<BlackNumberInfo> findAll(){
        List<BlackNumberInfo> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc",null);

        while (cursor.moveToNext()){
            BlackNumberInfo info = new BlackNumberInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            info.setMode(mode);
            info.setNumber(number);
            result.add(info);
        }
        cursor.close();
        db.close();
        return result;
    }

}
