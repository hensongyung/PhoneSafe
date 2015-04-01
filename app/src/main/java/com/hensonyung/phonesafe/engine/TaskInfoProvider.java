package com.hensonyung.phonesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.hensonyung.phonesafe.R;
import com.hensonyung.phonesafe.domain.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wengjiasheng on 2015/4/1.
 */
public class TaskInfoProvider {
    public static List<TaskInfo> getTaskInfo(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> processInfos =am.getRunningAppProcesses();
        List<TaskInfo> taskInfos = new ArrayList<>();
        for (ActivityManager.RunningAppProcessInfo processInfo :processInfos){
            TaskInfo taskInfo = new TaskInfo();

            String packname = processInfo.processName;
            taskInfo.setPackname(packname);
            Debug.MemoryInfo[] memoryInfos =am.getProcessMemoryInfo(new int[]{processInfo.pid});
            long memSize = memoryInfos[0].getTotalPrivateDirty()*1024;
            taskInfo.setMemSize(memSize);
            try {
//                pm.getPackageInfo(packname,0).applicationInfo;
                ApplicationInfo applicationInfo =pm.getApplicationInfo(packname, 0);
                Drawable icon = applicationInfo.loadIcon(pm);
                taskInfo.setIcon(icon);
                String name = applicationInfo.loadLabel(pm).toString();
                taskInfo.setName(name);
                if ((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM) ==0){
                    //用户进程
                    taskInfo.setUserTask(true);
                }else {
                    taskInfo.setUserTask(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
                taskInfo.setName(packname);
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
