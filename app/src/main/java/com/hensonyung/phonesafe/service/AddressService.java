package com.hensonyung.phonesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.hensonyung.phonesafe.R;
import com.hensonyung.phonesafe.db.dao.NumberAddressQueryUtils;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class AddressService extends Service {
    private SharedPreferences sp;

    private WindowManager wm;
    private View view;

    private TelephonyManager tm;
    private MyPhoneStateListener listener;
    private OutCallReceiver receiver;

    private WindowManager.LayoutParams params;
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
//            Toast.makeText(context,address,Toast.LENGTH_LONG).show();
            myToast(address);
        }
    }



    private class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    String address = NumberAddressQueryUtils.queryNumber(incomingNumber);
//                    Toast.makeText(getApplicationContext(),address,Toast.LENGTH_SHORT).show();
                    myToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (view !=null) {
                        wm.removeView(view);
                    }
                    break;

                default:

                    break;
            }
        }
    }


    long[] mHits = new long[2];
    //自定义吐司
    private void myToast(String address) {
        sp = getSharedPreferences("config",MODE_PRIVATE);
        view = View.inflate(this, R.layout.address_show,null);
        TextView textView = (TextView) view.findViewById(R.id.tv_address);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits,1,mHits,0,mHits.length-1);
                mHits[mHits.length-1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis()-500)){  //两次点击小于500ms
                    params.x = (wm.getDefaultDisplay().getWidth()-view.getWidth())/2;
                    params.y = (wm.getDefaultDisplay().getHeight() - view.getHeight())/2;
                    wm.updateViewLayout(view,params);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("lastX",params.x);
                    editor.putInt("lastY",params.y);
                    editor.commit();
                }
            }
        });

        //设置触摸监听器
        view.setOnTouchListener(new View.OnTouchListener() {
            int startX;
            int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int newX= (int) event.getRawX();
                        int newY = (int) event.getRawY();
                        int dx = newX - startX;
                        int dy = newY - startY;

                        params.x +=dx;
                        params.y +=dy;

                        if (params.x < 0){
                            params.x = 0;
                        }
                        if (params.y<0){
                            params.y = 0;
                        }
                        if (params.x>(wm.getDefaultDisplay().getWidth() - view.getWidth())){
                        params.x=(wm.getDefaultDisplay().getWidth() - view.getWidth());
                        }
                        if (params.y >(wm.getDefaultDisplay().getHeight() - view.getHeight())){
                        params.y = (wm.getDefaultDisplay().getHeight() - view.getHeight());
                        }
                        wm.updateViewLayout(view,params);

                        //重新初始化手指的初使结束位置
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("lastX",params.x);
                        editor.putInt("lastY",params.y);
                        editor.commit();
                        break;
                    default:
                        break;
                }
                return false;//事件处理完毕
            }
        });


        int []ids= {R.drawable.abc_ab_share_pack_holo_dark,R.drawable.atools,
        R.drawable.abc_btn_check_material,R.drawable.abc_btn_radio_material,R.drawable.abc_ic_clear_mtrl_alpha};
        SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);

        textView.setText(address);
        view.setBackgroundResource(ids[sp.getInt("which",0)]);
//        view = new TextView(getApplicationContext());
//        view.setText(address);
//        view.setTextSize(22);
//        view.setTextColor(Color.RED);

        params = new WindowManager.LayoutParams();

        //窗体参数
        params.height =WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        //控制位置
        params.gravity = Gravity.TOP+Gravity.LEFT; //窗体对其左上角
        params.x = sp.getInt("lastX",0);
        params.y = sp.getInt("lastY",0);

        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                |WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;//半透明
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;

        //电话优先级的一种窗体类型,要添加权限  SYSTEM_ALERT_WINDOW
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

        wm.addView(view,params);
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
        registerReceiver(receiver, filter);

        //实例化窗体
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
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
