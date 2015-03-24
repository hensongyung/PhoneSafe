package com.hensonyung.phonesafe;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;


public class Setup1Activity extends BaseSetupActivity {
    private GestureDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                if ((e2.getRawX() -e1.getRawX()) >200){
                    return true;
                }
                if ((e1.getRawX()-e2.getRawX()) >200){
                    showNext();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    public void showNext(){
        Intent intent = new Intent(this,Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
    }

    @Override
    public void showPre() {

    }

//    public void next(View view){
//        Intent intent = new Intent(this,Setup2Activity.class);
//        startActivity(intent);
//        finish();
//        //要在finish后面执行
//        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
