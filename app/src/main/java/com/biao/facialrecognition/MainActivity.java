package com.biao.facialrecognition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.biao.facialrecognition.baidu.FacielBaiduActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void start(View view) {
        Intent intent = new Intent(this, FacielActivity.class);
        startActivity(intent);
    }

    public void start_baidu(View view) {
        Intent intent = new Intent(this, FacielBaiduActivity.class);
        startActivity(intent);
    }
}
