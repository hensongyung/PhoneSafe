package com.hensonyung.phonesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 系统信息工具类
 */
public class SystemInfoUtils {
    /**
     * 获取正在运行的进程的数量
     * @return
     */
    public static int getRunningProcessCount(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        return infos.size();
    }

    /**
     * 内存
     * @param context
     * @return
     */
    public static long getAvailMem(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outinfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outinfo);
        return outinfo.availMem;


    }

    public static long getTotalMem(Context context){
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo outinfo = new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(outinfo);
//        return outinfo.totalMem;


        try {
            File file = new File("/proc/meminfo");
            FileInputStream fis =new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            for (char c : line.toCharArray()){
                if (c>='0'&&c<='9'){
                    sb.append(c);
                }

            }
            return Long.parseLong(sb.toString())*1024;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }
}
