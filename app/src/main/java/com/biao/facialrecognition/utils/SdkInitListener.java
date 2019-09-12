package com.biao.facialrecognition.utils;

/**
 * Created by benxiang on 2019/9/7.
 */

public interface SdkInitListener {
    void initStart();

    void initLicenseSuccess();

    void initLicenseFail(int errorCode, String msg);

    void initModelSuccess();

    void initModelFail(int errorCode, String msg);
}
