package com.hensonyung.phonesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.hensonyung.phonesafe.db.dao.NumberAddressQueryUtils;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class AddressService extends Service {
    private WindowManager wm;
    private View view;

    private TelephonyManager tm;
    private MyPhoneStateListener listener;
    private OutCallReceiver receiver;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    //服务里面的内部类。
    class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
            String phone = getResultData();
            String address = NumberAddressQueryUtils.queryNumber(phone);
            Toast.makeText(context,address,Toast.LENGTH_LONG).show();
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    String address = NumberAddressQueryUtils.queryNumber(incomingNumber);
                    Toast.makeText(getApplicationContext(),address,Toast.LENGTH_SHORT).show();
                    break;
                default:

                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //监听来电
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        //用服务去注册广播接受者
        receiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(receiver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        listener = null;

        //取消注册广播
        unregisterReceiver(receiver);
        receiver=null;
    }
}
