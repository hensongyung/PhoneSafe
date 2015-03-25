package com.hensonyung.phonesafe;

import android.app.Activity;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hensonyung.phonesafe.db.dao.NumberAddressQueryUtils;


public class NumberAddressQueryActivity extends Activity {
    private EditText et_phone;
    private TextView tv_result;

    private Vibrator vibrator;

    LinearLayout ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address_query);
        ll= (LinearLayout) findViewById(R.id.ll);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        et_phone = (EditText) findViewById(R.id.et_phone);
        tv_result = (TextView) findViewById(R.id.tv_result);
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s!=null && s.length()>3){

                    tv_result.setText(NumberAddressQueryUtils.queryNumber(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void numberAddressQuery(View view){
        String phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this,"号码不能为空",Toast.LENGTH_SHORT).show();
            Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);

            ll.startAnimation(shake);

//            vibrator.vibrate(2000);
            long[]pattern = {200,200};
            vibrator.vibrate(pattern,-1);//-1为不重复,从0循环震动,从1
            return;
        }else {
            String address = NumberAddressQueryUtils.queryNumber(phone);
            tv_result.setText(address);
        }
    }

}
