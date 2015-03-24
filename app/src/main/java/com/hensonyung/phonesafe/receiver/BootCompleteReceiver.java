package com.hensonyung.phonesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {
    private SharedPreferences sp;
    private TelephonyManager tm;


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        boolean protecting =sp.getBoolean("protecting",false);
        if (protecting){
            tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);


            String save =sp.getString("sim","")+"advsd";

            String realSim =tm.getSimSerialNumber();

            if (save.equals(realSim)){
                Toast.makeText(context,"sim卡没变",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context,"sim卡已经变更",Toast.LENGTH_SHORT).show();
                String safenumber = sp.getString("safenumber","");
                SmsManager.getDefault().sendTextMessage(safenumber,null,"sim卡已变更...",null,null);

            }
        }




    }
}
