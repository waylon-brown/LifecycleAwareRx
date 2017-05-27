package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.LifecycleOwner;

import io.reactivex.SingleObserver;
import io.reactivex.SingleTransformer;

public class LifecycleBinder {

    /**
     * Only handles destroying
     * 
     * @param lifecycleOwner
     * @return
     */
    public static LifecycleTransformer bindToLifecycle(LifecycleOwner lifecycleOwner) {
        return new LifecycleTransformer(lifecycleOwner, null);
    }

    // NOTE: can also pass in DisposableSingleObserver
    public static <T> SingleTransformer<T, T> bindToLifecycle(LifecycleOwner lifecycleOwner,
                                                           SingleObserver<T> singleObserver) {
        return new LifecycleTransformer<>(lifecycleOwner, new SingleWrapper<>(singleObserver));
    }
}
