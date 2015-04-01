package com.hensonyung.phonesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.hensonyung.phonesafe.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wengjiasheng on 2015/3/30.
 */
public class AppInfoProvider {
    public static List<AppInfo> getAppInfos(Context context){
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<>();
        for (PackageInfo packageInfo : packageInfos){
            AppInfo appInfo =new AppInfo();
            //packageInfo 相当于一个应用程序的apk包的清单文件
            String packname = packageInfo.packageName;
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
            int flags = packageInfo.applicationInfo.flags;
            if ((flags& ApplicationInfo.FLAG_SYSTEM) ==0){
                //用户程序
                appInfo.setUserApp(true);
            }else {
                //系统程序
                appInfo.setUserApp(false);
            }
            if (((flags& ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0)){
                //手机内存
                appInfo.setInRom(true);
            }else {
                //手机sd
                appInfo.setInRom(false);
            }

            appInfo.setPackname(packname);
            appInfo.setIcon(icon);
            appInfo.setName(name);
            appInfos.add(appInfo);

        }
        return appInfos;
    }
}
