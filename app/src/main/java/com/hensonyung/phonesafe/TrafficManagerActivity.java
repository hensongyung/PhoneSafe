package com.hensonyung.phonesafe;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;


public class TrafficManagerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager);
        PackageManager pm= getPackageManager();
        List<ApplicationInfo> applicationInfos=pm.getInstalledApplications(0);
        for (ApplicationInfo info :applicationInfos){
            int uid =info.uid;
            long tx=TrafficStats.getUidTxBytes(uid);
            long rx=TrafficStats.getUidRxBytes(uid);

        }
        TrafficStats.getMobileTxBytes();
        TrafficStats.getMobileRxBytes();

        TrafficStats.getTotalTxBytes();
        TrafficStats.getTotalRxBytes();
    }



}
