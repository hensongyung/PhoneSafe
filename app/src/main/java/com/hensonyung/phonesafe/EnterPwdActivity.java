package com.hensonyung.phonesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class EnterPwdActivity extends Activity {
    private EditText et_pwd;
    private String packname;

    private TextView tv_name;
    private ImageView iv_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        et_pwd = (EditText) findViewById(R.id.et_pwd);

        Intent intent =getIntent();
        //当前要保护的应用程序包名
        packname = intent.getStringExtra("packname");
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);

        PackageManager packageManager = getPackageManager();
        try {
            ApplicationInfo info = packageManager.getApplicationInfo(packname, 0);
            tv_name.setText(info.loadLabel(packageManager));
            iv_icon.setImageDrawable(info.loadIcon(packageManager));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void click(View view){
        String pwd = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)){
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if ("123".equals(pwd)){
            //告诉看门狗，不去保护了
            //自定义广播
            Intent intent = new Intent();
            intent.setAction("com.hensonyung.phonesafe.tempstop");
            intent.putExtra("packname",packname);
            sendBroadcast(intent);
            finish();
        }else {
            Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed() {
        //回桌面,所有activity最小化,不会执行onDestory
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
