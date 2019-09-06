package com.biao.facialrecognition.retrofit;


import com.biao.facialrecognition.model.FaceQueryBen;
import com.biao.facialrecognition.model.FacetoFaceBean;
import com.biao.facialrecognition.model.GetTonken;
import com.biao.facialrecognition.preference.SharedPreferencesUtil;

import java.util.List;
import java.util.TreeMap;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * 后台接口
 * Created by benxiang on 2019/4/9.
 */

public interface RetrofitInterface {
    //获取百度token
    @GET("oauth/2.0/token")
    Observable<GetTonken> getTonken(@QueryMap() TreeMap<String, String> map);

    //人证对比
    @POST("rest/2.0/face/v3/match")
    Observable<FacetoFaceBean> faceToFace(@Query("access_token") String token, @Body List<FaceQueryBen> json);
}