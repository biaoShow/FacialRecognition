package com.biao.facialrecognition.retrofit;


import com.biao.facialrecognition.BuildConfig;
import com.biao.facialrecognition.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * retrofit+rxjava+okhttp封装类
 * Created by benxiang on 2019/4/9.
 */

public class RetrofitHelper {

    private Retrofit mRetrofit = null;
    private RetrofitInterface retrofitInterface = null;
    private static RetrofitHelper retrofitHelper = null;
    private static final long DEFAULT_TIME_OUT = 30;
    private static final long CONNECT_TIME_OUT = 12;

    //throw a custom IOException("Unexpected protocol: " + protocol)
    private static final String CUSTOM_REPEAT_REQ_PROTOCOL = "MY_CUSTOM_REPEAT_REQ_PROTOCOL";
    //Value 里面保存的是时间
    public static Map<String, Long> requestIdsMap = new HashMap<>();

    private RetrofitHelper() {
        if (null == mRetrofit) {
            mRetrofit = new Retrofit.Builder()
                    .client(setOkHtttp())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BuildConfig.BASE_URL)
                    .build();
        }
    }

    public static RetrofitHelper getInstance() {
        if (null == retrofitHelper) {
            retrofitHelper = new RetrofitHelper();
        }
        return retrofitHelper;
    }

    public RetrofitInterface getRetrofitInterface() {
        if (null == retrofitInterface) {
            retrofitInterface = mRetrofit.create(RetrofitInterface.class);
        }
        return retrofitInterface;
    }

    private OkHttpClient setOkHtttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);//连接 超时时间
        builder.writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//写操作 超时时间
        builder.readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//读操作 超时时间
//        builder.addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(@NonNull Chain chain) throws IOException {
//                Request request = chain.request()
//                        .newBuilder()
//                        .addHeader("aisle-modle", Constant.MEDICINE_NUM)
//                        .build();
//                return chain.proceed(request);
//            }
//        });
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addNetworkInterceptor(logInterceptor);
        }
        builder.retryOnConnectionFailure(true);//错误重连
        return builder.build();
    }

    public class HttpLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            LogUtil.d(message);
        }
    }
}
