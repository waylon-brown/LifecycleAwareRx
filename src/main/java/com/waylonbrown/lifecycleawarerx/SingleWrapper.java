package com.waylonbrown.lifecycleawarerx;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Single;
import io.reactivex.SingleObserver;

/**
 * Wraps a {@link io.reactivex.Single} with it's {@link io.reactivex.SingleObserver}.
 */
public class SingleWrapper<T> implements BaseReactiveTypeWithObserver<T> {

    @Nullable private Single<T> single;
    @NonNull private final SingleObserver<T> singleObserver;

    SingleWrapper(@NonNull SingleObserver<T> singleObserver) {
        this.singleObserver = singleObserver;
    }
    
    public void setSingle(Single<T> single) {
        this.single = single;
    }

    @NonNull
    public Single<T> getSingle() {
        return single;
    }

    @NonNull
    public SingleObserver<T> getSingleObserver() {
        return singleObserver;
    }
}
