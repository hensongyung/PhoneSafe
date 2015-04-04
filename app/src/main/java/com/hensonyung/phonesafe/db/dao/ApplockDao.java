package com.hensonyung.phonesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hensonyung.phonesafe.db.ApplockDBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 程序锁的增删改查
 */
public class ApplockDao {
    private ApplockDBOpenHelper helper;
    private Context context;
    public ApplockDao(Context context){
        helper = new ApplockDBOpenHelper(context);
        this.context =context;
    }

    /**
     * 添加锁定程序的包名
     */
    public void add(String packname){
        SQLiteDatabase db =helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packname",packname);
        db.insert("applock",null,values);
        db.close();
        Intent intent = new Intent();
        intent.setAction("com.hensonyung.phonesafe.applockchange");
        context.sendBroadcast(intent);
//        context.getContentResolver().notifyChange();
    }

    public void delete(String packname){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("applock","packname=?",new String[]{packname});
        db.close();
    }

    public boolean find(String packname){
        SQLiteDatabase db = helper.getReadableDatabase();
        boolean result=false;
        Cursor cursor =db.query("applock", null, "packname=?", new String[]{packname}, null, null, null);
        if (cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }
    public List<String> findAll(){
        List<String> protectPacknames = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        boolean result=false;
        Cursor cursor =db.query("applock", new String[]{"packname"}, null, null, null, null,null);
        if (cursor.moveToNext()){
            protectPacknames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return protectPacknames;
    }
}
