package com.hensonyung.phonesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class KillAllReceiver extends BroadcastReceiver {
    public KillAllReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        System.out.println("受到广播了");
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info:infos){
            activityManager.killBackgroundProcesses(info.processName);
        }
    }
}
