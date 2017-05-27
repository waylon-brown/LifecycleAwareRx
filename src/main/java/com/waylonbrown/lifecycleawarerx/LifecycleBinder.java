package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.LifecycleOwner;

import com.waylonbrown.lifecycleawarerx.reactivetypes.CompletableWithObserver;
import com.waylonbrown.lifecycleawarerx.reactivetypes.MaybeWithObserver;
import com.waylonbrown.lifecycleawarerx.reactivetypes.ObservableWithObserver;
import com.waylonbrown.lifecycleawarerx.reactivetypes.SingleWithObserver;

import io.reactivex.CompletableObserver;
import io.reactivex.CompletableTransformer;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.SingleTransformer;

public class LifecycleBinder {

    /**
     * Only handles destroying
     * 
     * @param lifecycleOwner
     * @return
     */
    public static LifecycleTransformer disposeOnDestroy(LifecycleOwner lifecycleOwner) {
        return new LifecycleTransformer(lifecycleOwner, null);
    }

    // NOTE: can also pass in DisposableSingleObserver
    // TODO: move these 4 into one method or no?
    public static <T> SingleTransformer<T, T> bindToLifecycle(LifecycleOwner lifecycleOwner,
                                                              SingleObserver<T> singleObserver) {
        return new LifecycleTransformer<>(lifecycleOwner, new SingleWithObserver<>(singleObserver));
    }

    public static <T> ObservableTransformer<T, T> bindToLifecycle(LifecycleOwner lifecycleOwner,
                                                                  Observer<T> observer) {
        return new LifecycleTransformer<>(lifecycleOwner, new ObservableWithObserver<>(observer));
    }

    public static <T> MaybeTransformer<T, T> bindToLifecycle(LifecycleOwner lifecycleOwner,
                                                             MaybeObserver<T> observer) {
        return new LifecycleTransformer<>(lifecycleOwner, new MaybeWithObserver<>(observer));
    }

    public static <T> CompletableTransformer bindToLifecycle(LifecycleOwner lifecycleOwner,
                                                                   CompletableObserver observer) {
        return new LifecycleTransformer<>(lifecycleOwner, new CompletableWithObserver(observer));
    }
}
