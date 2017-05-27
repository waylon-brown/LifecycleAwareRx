package com.waylonbrown.lifecycleawarerx;

import android.support.annotation.NonNull;

import io.reactivex.Single;
import io.reactivex.SingleObserver;

/**
 * Wraps a {@link io.reactivex.Single} with it's {@link io.reactivex.SingleObserver}.
 */
public class SingleWrapper<T> implements BaseReactiveTypeWithObserver<T> {

    @NonNull private final Single<T> single;
    @NonNull private final SingleObserver<T> singleObserver;

    SingleWrapper(Single<T> single, SingleObserver<T> singleObserver) {
        this.single = single;
        this.singleObserver = singleObserver;
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
