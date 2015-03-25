package com.hensonyung.phonesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by wengjiasheng on 2015/3/25.
 */
public class ServiceUtils {
    /**
     * 校验某个服务是否正常
     */
    public static boolean isServiceRunning(Context context,String serviceName){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos =am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info :infos){
            String name = info.service.getClassName();
            if (serviceName.equals(name)){
                return true;
            }
        }

        return false;
    }
}
