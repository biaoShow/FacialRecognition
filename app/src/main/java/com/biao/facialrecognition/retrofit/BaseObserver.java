package com.biao.facialrecognition.retrofit;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

/**
 * 网络请求 ，管理订约
 */
public abstract class BaseObserver<T> extends DisposableObserver<T> {
    public BaseObserver(CompositeDisposable compositeDisposable) {
        compositeDisposable.add(this);
    }
}
