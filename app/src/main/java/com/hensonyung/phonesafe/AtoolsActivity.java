package com.hensonyung.phonesafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hensonyung.phonesafe.utils.SmsUtils;


public class AtoolsActivity extends Activity {
    private ProgressDialog pd;
    private ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
//        pb = (ProgressBar) findViewById(R.id.pb);

    }


    public void numberQuery(View view){
        Intent intent = new Intent(this,NumberAddressQueryActivity.class);
        startActivity(intent);

    }

    public void smsBackup(View view){
        pd = new ProgressDialog(this);
        pd.setMessage("正在备份短信");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SmsUtils.backupSms(getApplicationContext(),new SmsUtils.BackupCallBack() {
                        @Override
                        public void beforeBackup(int max) {
//                            pb.setMax(max);
                            pd.setMax(max);

                        }

                        @Override
                        public void onSmsBackup(int progress) {
//                            pb.setProgress(progress);
                            pd.setProgress(progress);
                        }
                    });
//                    Toast.makeText(AtoolsActivity.this,"备份成功",Toast.LENGTH_SHORT).show();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"备份成功",Toast.LENGTH_SHORT).show();

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(AtoolsActivity.this,"备份失败",Toast.LENGTH_SHORT).show();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"备份失败",Toast.LENGTH_SHORT).show();

                        }
                    });

                }finally {
                    pd.dismiss();
                }
            }
        }).start();
    }
    public void smsRestore(View view){
        SmsUtils.smsRestore(this,true);
        Toast.makeText(this,"还原成功",Toast.LENGTH_SHORT).show();
    }
}
