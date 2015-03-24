package com.hensonyung.phonesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;


public class Setup4Activity extends BaseSetupActivity {
    SharedPreferences sp;

    private CheckBox cb_protect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        cb_protect = (CheckBox) findViewById(R.id.cb_protect);

        boolean protecting = sp.getBoolean("protecting",false);

        if (protecting){
            cb_protect.setText("手机防盗已开启");
            cb_protect.setChecked(protecting);
        }else {
            cb_protect.setText("手机防盗没有开启");
            cb_protect.setChecked(protecting);
        }

        cb_protect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_protect.setText("手机防盗已开启");

                }else {
                    cb_protect.setText("手机防盗没有开启");
                }
                SharedPreferences.Editor editor =sp.edit();
                editor.putBoolean("protecting",isChecked);
                editor.commit();
            }
        });
    }

    public void showNext(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("configed",true);
        editor.commit();
        Intent intent = new Intent(this,LostFindActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this,Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_tran_in,R.anim.pre_tran_out);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
