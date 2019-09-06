package com.biao.facialrecognition.retrofit;

import java.util.TreeMap;

/**
 * http传输参数拼装
 * Created by benxiang on 2019/5/25.
 */

public class RetrofitMap {
    /**
     * 获取tanken
     */
    public static TreeMap<String, String> getTanken() {
        TreeMap<String, String> signMap = new TreeMap<>();
        signMap.put("grant_type", "client_credentials");
        signMap.put("client_id", "APP_ID");
        signMap.put("client_secret", "APP_KEY");
        return signMap;
    }
}
