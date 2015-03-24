package com.hensonyung.phonesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class LostFindActivity extends Activity {
    private SharedPreferences sp;
    private TextView tv_safenumber;
    private ImageView iv_protecting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        boolean configed =sp.getBoolean("configed",false);
        if (configed){
            setContentView(R.layout.activity_lost_find);
            tv_safenumber = (TextView) findViewById(R.id.tv_safenumber);
            iv_protecting = (ImageView) findViewById(R.id.iv_protecting);
            String safenumber = sp.getString("safenumber","");
            tv_safenumber.setText(safenumber);

            boolean protecting =sp.getBoolean("protecting",false);
            if (protecting){
                iv_protecting.setImageResource(R.drawable.lock);
            }else {
                iv_protecting.setImageResource(R.drawable.unlock);
            }
        }else {
            //跳转到设置向导页面
            Intent intent = new Intent(this,Setup1Activity.class);
            startActivity(intent);
            finish();//关闭当前页面
        }

    }

    public void reEnterSetup(View view){
        Intent intent = new Intent(this,Setup1Activity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lost_find, menu);
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
