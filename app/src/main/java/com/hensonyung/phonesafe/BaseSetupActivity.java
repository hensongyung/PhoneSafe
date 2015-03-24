package com.hensonyung.phonesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by wengjiasheng on 2015/3/23.
 */
public abstract class BaseSetupActivity extends Activity {
    private GestureDetector detector;

    protected SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("config",MODE_PRIVATE);

        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) <200){
                    return true;
                }

                //屏蔽斜划
                if (Math.abs(e2.getRawY()-e1.getRawY()) >100){
                    return true;
                }

                if ((e2.getRawX() -e1.getRawX()) >200){
                    showPre();
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

    public void next(View view){
        showNext();
    }

    public abstract void showNext();
    public abstract void showPre();

    public void previous(View view){
        showPre();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

}
