package com.biao.facialrecognition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.FaceDetect;
import com.baidu.idl.main.facesdk.FaceFeature;
import com.biao.facialrecognition.baidu.FacielBaiduActivity;
import com.biao.facialrecognition.baidu.FacileBaiduOfflineActivity;
import com.biao.facialrecognition.preference.PreferencesKey;
import com.biao.facialrecognition.preference.SharedPreferencesUtil;
import com.biao.facialrecognition.utils.FaceSDKManager;
import com.biao.facialrecognition.utils.LogUtil;

public class MainActivity extends AppCompatActivity {


    private FaceSDKManager faceSDKManager;
    private SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        faceSDKManager = FaceSDKManager.getInstance();
        sharedPreferencesUtil = SharedPreferencesUtil.getIntent(this);
    }

    public void start(View view) {
        Intent intent = new Intent(this, FacielActivity.class);
        startActivity(intent);
    }

    public void start_baidu(View view) {
        Intent intent = new Intent(this, FacielBaiduActivity.class);
        startActivity(intent);
    }

    public void start_baidu_activate(View view) {
        if (sharedPreferencesUtil.getString(PreferencesKey.BAIDU_SDK) == null) {
            faceSDKManager.activateSDK(getApplicationContext(), sharedPreferencesUtil);
        } else {
            LogUtil.i("已经激活，无需重新激活");
            faceSDKManager.authentication(getApplicationContext());
        }
    }

    public void start_baidu_offline(View view) {
//        authentication();
        Intent intent = new Intent(MainActivity.this, FacileBaiduOfflineActivity.class);
        startActivity(intent);
    }
}
