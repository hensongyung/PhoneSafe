package com.hensonyung.phonesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wengjiasheng on 2015/3/25.
 */
public class NumberAddressQueryUtils {
    private static String path = "data/data/com.hensonyung.phonesafe/files/address.db";
    public static String queryNumber(String number){
        String address = number;

        SQLiteDatabase database =SQLiteDatabase.openDatabase(path,null, SQLiteDatabase.OPEN_READONLY);
        //正则表达式
        if (number.matches("^1[34568]\\d{9}$")){
            //手机号码


            Cursor cursor =database.rawQuery("select location from data2 where id = " +
                            "(select outkey from data1 where id = ?)",
                    new String[]{number.substring(0, 7)});

            while (cursor.moveToNext()){
                String location =cursor.getString(0);
                address = location;
            }
            cursor.close();
        }else {
            switch (number.length()){
                case 3:
                    address ="匪警号码";
                    break;
                case 4:
                    address="模拟器";
                    break;
                case 5:
                    address="客服号码";
                    break;
                case 8:
                    address="本地号码";
                    break;
                case 7:
                    address="本地号码";
                    break;
                default:
                    if (number.length()>10&&number.startsWith("0")){
                        Cursor cursor =database.rawQuery("select location from data2 where area = ?",
                                new String[]{number.substring(1,3)});
                        while (cursor.moveToNext()){
                            String location = cursor.getString(0);
//                            location.substring(0,location.length()-2);
                            address = location;
                        }
                        cursor.close();

                        database.rawQuery("select location from data2 where area = ?",
                                new String[]{number.substring(1,4)});
                        while (cursor.moveToNext()){
                            String location = cursor.getString(0);
                            address = location;
                        }
                    }
                    break;
            }
        }


        return address;
    }
}
