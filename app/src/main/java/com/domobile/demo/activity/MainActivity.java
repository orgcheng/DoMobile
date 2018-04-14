package com.domobile.demo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.domobile.demo.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void launcherIconParse(View view){
        startActivity(new Intent(this, LauncherIconParseActivity.class));
    }

    public void exchangeRateHelper(View view){
        startActivity(new Intent(this, ExchangeRateHelperActivity.class));
    }
}
