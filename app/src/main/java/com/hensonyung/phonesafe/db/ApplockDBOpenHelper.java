package com.hensonyung.phonesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wengjiasheng on 2015/3/27.
 */
public class ApplockDBOpenHelper extends SQLiteOpenHelper{
    //数据库创建
    public ApplockDBOpenHelper(Context context) {
        super(context, "applock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table applock (_id integer primary key autoincrement,packname varchar(20))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
