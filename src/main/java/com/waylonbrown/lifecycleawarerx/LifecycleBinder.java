package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.LifecycleOwner;

import io.reactivex.SingleTransformer;
import io.reactivex.observers.DisposableSingleObserver;

public class LifecycleBinder {

    public static <T> SingleTransformer<T, T> bindToLifecycle(LifecycleOwner lifecycleOwner,
                                                           DisposableSingleObserver<T> disposableSingleObserver) {
        return new LifecycleTransformer<>(lifecycleOwner, disposableSingleObserver);
    }
}
