package com.biao.facialrecognition.retrofit;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by benxiang on 2019/5/25.
 */

public class RxUtil {
    public static <T> ObservableTransformer<T, T> rxIoToMain() {//使用compose切换线程简化
        ObservableTransformer<T, T> observableTransformer = new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
        return observableTransformer;
    }
}
