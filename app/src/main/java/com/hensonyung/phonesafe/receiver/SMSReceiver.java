package com.hensonyung.phonesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.hensonyung.phonesafe.R;
import com.hensonyung.phonesafe.service.GPSService;

public class SMSReceiver extends BroadcastReceiver {
    private SharedPreferences sp;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Object [] objs = (Object[]) intent.getExtras().get("pdus");
        sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        String safenumber =sp.getString("safenumber","");


        for (Object b : objs){
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);
            String sender = sms.getOriginatingAddress();
            String body = sms.getMessageBody();

            if (sender.contains(safenumber)){
                if ("#*location*#".equals(body)){
                    System.out.println("location");
                    Intent i = new Intent(context, GPSService.class);
                    context.startService(i);

                    SharedPreferences sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
                    String lastlocation = sp.getString("lastlocation",null);
                    if (TextUtils.isEmpty(lastlocation)){
                        SmsManager.getDefault().sendTextMessage(sender,null,"getting location",null,null);
                    }else {
                        SmsManager.getDefault().sendTextMessage(sender,null,lastlocation,null,null);
                    }
                    System.out.println(lastlocation);
                    abortBroadcast();
                }else if ("#*alarm*#".equals(body)){

                    MediaPlayer player = MediaPlayer.create(context, R.raw.wdhbx);
                    player.setLooping(false);
                    player.setVolume(1.0f,1.0f);
                    player.start();
//                    abortBroadcast();
                    System.out.println("alarm");
                }else if ("#*wipedata*#".equals(body)){
                    System.out.println("wipedata");
                    abortBroadcast();
                }else if ("#*locksreen*#".equals(body)){
                    System.out.println("lock");
                    abortBroadcast();
                }
            }

        }
    }
}
