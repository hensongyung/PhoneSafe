package com.hensonyung.phonesafe;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class AtoolsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }


    public void numberQuery(View view){
        Intent intent = new Intent(this,NumberAddressQueryActivity.class);
        startActivity(intent);
    }
}
