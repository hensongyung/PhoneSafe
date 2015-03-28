package com.hensonyung.phonesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.hensonyung.phonesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class CallSmsSafeService extends Service {
    private InnerSmsReceiver receiver;
    private BlackNumberDao dao;
    private MyListener listener;

    private TelephonyManager tm;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    private class InnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs){
                SmsMessage smsMessage =SmsMessage.createFromPdu((byte[])obj);
                String sender = smsMessage.getOriginatingAddress();
                String result = dao.findMode(sender);

                if ("2".equals(result)||"3".equals(result)){

                    abortBroadcast();
                }
                String body = smsMessage.getMessageBody();
                if (body.contains("fapiao")){
                    abortBroadcast();
                    System.out.println("fapiao");
                }
            }

        }
    }

    @Override
    public void onCreate() {
        dao = new BlackNumberDao(this);


        listener = new MyListener();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        receiver = new InnerSmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(receiver,filter);



        super.onCreate();
    }

    private class CallLogObserver extends ContentObserver{
        private String incomingNumber;
        @Override
        public void onChange(boolean selfChange) {
            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber);
            super.onChange(selfChange);
        }

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public CallLogObserver(String incomingNumber,Handler handler) {

            super(handler);
            this.incomingNumber = incomingNumber;


        }
    }
    private class MyListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    String result = dao.findMode(incomingNumber);
                    if ("1".equals(result)||"3".equals(result)){
                        //利用反射

                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri,true,new CallLogObserver(incomingNumber,new Handler()));
                        endCall();
//                        deleteCallLog(incomingNumber);//删除来电信息
                    }
                    break;

            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void deleteCallLog(String incomingNumber) {
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");
//        CallLog.CONTENT_URI
        resolver.delete(uri,"number = ?",new String[]{incomingNumber});
    }

    public void endCall(){
        try {
            Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService",String.class);
            IBinder iBinder = (IBinder) method.invoke(null,TELEPHONY_SERVICE);
            ITelephony.Stub.asInterface(iBinder).endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {

        unregisterReceiver(receiver);
        receiver = null;
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
//        listener = null;
        super.onDestroy();
    }
}
