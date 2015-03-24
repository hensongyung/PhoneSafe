package com.hensonyung.phonesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hensonyung.phonesafe.ui.SettingItemView;


public class Setup2Activity extends BaseSetupActivity {
    private SettingItemView siv;

    //读取手机sim卡的信息
    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        siv = (SettingItemView) findViewById(R.id.siv_setup2_sim);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        String sim =sp.getString("sim",null);
        if (TextUtils.isEmpty(sim)){
            siv.setChecked(false);

        }else {
            siv.setChecked(true);
        }
        siv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();

                if (siv.isChecked()){
                    siv.setChecked(false);
                    editor.putString("sim", null);

                }else {
                    String sim = tm.getSimSerialNumber();
                    siv.setChecked(true);
                    editor.putString("sim",sim);

                }
                editor.commit();

            }
        });


    }




    @Override
    public void showNext() {
        if (!siv.isChecked()){
            Toast.makeText(this,"没有绑定sim卡",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this,Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this,Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_tran_in,R.anim.pre_tran_out);
    }




}
