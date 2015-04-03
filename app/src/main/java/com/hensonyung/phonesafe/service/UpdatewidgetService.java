package com.hensonyung.phonesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.hensonyung.phonesafe.R;
import com.hensonyung.phonesafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

public class UpdatewidgetService extends Service {
    private Timer timer;
    private TimerTask task;

    private ScreenOnReceiver onReceiver;
    private ScreenOffReceiver offReceiver;

    //widget管理器
    private AppWidgetManager awm;

    public UpdatewidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class ScreenOffReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            stopTimer();
        }
    }

    private class ScreenOnReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            startTimer();
        }
    }


    @Override
    public void onCreate() {
        onReceiver = new ScreenOnReceiver();
        offReceiver = new ScreenOffReceiver();
        registerReceiver(onReceiver,new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(offReceiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
        awm =AppWidgetManager.getInstance(this);
        startTimer();
        super.onCreate();
    }

    private void startTimer() {
        if (timer ==null &&task==null){
            timer =new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("更新widget");
                    ComponentName provider = new ComponentName(UpdatewidgetService.this,MyAppWidget.class);
                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.my_app_widget);
                    views.setTextViewText(R.id.process_count,"正在运行的进程"+
                            SystemInfoUtils.getRunningProcessCount(getApplicationContext())+"个");

                    long size = SystemInfoUtils.getAvailMem(getApplicationContext());
                    views.setTextViewText(R.id.process_memory,"可用内存"+ Formatter.formatFileSize(getApplicationContext(),size));
                    //描述一个动作，这个动作是由另外一个程序执行的。
                    //自定义一个广播事件，杀死后台进度的事件\
                    Intent intent = new Intent();
                    intent.setAction("com.hensonyung.phonesafe.killall");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.btn_clean,pendingIntent);

                    awm.updateAppWidget(provider,views);

                }
            };
            timer.schedule(task,0,3000);
        }
    }

    private void stopTimer(){
        if (timer!=null&&task!=null){
            timer.cancel();
            task.cancel();
            timer=null;
            task=null;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(offReceiver);
        unregisterReceiver(onReceiver);
        offReceiver=null;
        onReceiver=null;
        stopTimer();
    }
}
