package com.hensonyung.phonesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.hensonyung.phonesafe.EnterPwdActivity;
import com.hensonyung.phonesafe.db.dao.ApplockDao;

import java.util.List;

/**
 * 监视系统程序运行状态
 */
public class WatchDogService extends Service {
    private ActivityManager am;
    private boolean flag;
    private ApplockDao dao;
    private InnerReceiver receiver;
    private String tempStopProtectPackname;
    private DataChangeReceiver dataChangeReceiver;

    private ScreenOffReceiver offReceiver;


    private Intent intent;
    private List<String> protectPacknames;
    public WatchDogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);


        offReceiver = new ScreenOffReceiver();
        dataChangeReceiver =new DataChangeReceiver();
        registerReceiver(offReceiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(dataChangeReceiver,new IntentFilter("com.hensonyung.phonesafe.applockchange"));
        receiver =new InnerReceiver();
        registerReceiver(receiver,new IntentFilter("com.hensonyung.phonesafe.tempstop"));
        flag = true;
        intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dao = new ApplockDao(this);
        protectPacknames =dao.findAll();
        new Thread(){
            @Override
            public void run() {
                while (flag){
                    List<ActivityManager.RunningTaskInfo> infos =am.getRunningTasks(1);
                    String packname =infos.get(0).topActivity.getPackageName();
                    System.out.println(packname);
//                    if (dao.find(packname)){  //数据库查询比较慢，改成内存查询
                    if (protectPacknames.contains(packname)){
                        if (packname.equals(tempStopProtectPackname)){

                        }else {
//                            Intent intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
                            //这个服务是没有任务栈信息的，在服务开启activity要制定这个activity运行的任务栈
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packname", packname);
                            startActivity(intent);
                        }
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        flag=false;
        unregisterReceiver(receiver);
        unregisterReceiver(dataChangeReceiver);
        unregisterReceiver(offReceiver);
        receiver=null;
        dataChangeReceiver=null;
        offReceiver =null;
        super.onDestroy();
    }

    private class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopProtectPackname =intent.getStringExtra("packname");


        }
    }

    private class ScreenOffReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopProtectPackname = null;
        }
    }
    private class DataChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            protectPacknames =dao.findAll();
        }
    }


}
