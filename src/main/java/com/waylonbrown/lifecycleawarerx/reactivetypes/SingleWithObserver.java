package com.waylonbrown.lifecycleawarerx.reactivetypes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Single;
import io.reactivex.SingleObserver;

/**
 * Wraps a {@link io.reactivex.Single} with it's {@link io.reactivex.SingleObserver}.
 */
public class SingleWithObserver<T> implements BaseReactiveTypeWithObserver<Single<T>, SingleObserver<T>> {

    @Nullable private Single<T> single;
    @NonNull private final SingleObserver<T> singleObserver;

    public SingleWithObserver(@NonNull SingleObserver<T> singleObserver) {
        this.singleObserver = singleObserver;
    }
    
    @Override
    public void setReactiveType(Single<T> single) {
        this.single = single;
    }

    @NonNull
    @Override
    public Single<T> getReactiveType() {
        return single;
    }

    @NonNull
    @Override
    public SingleObserver<T> getObserver() {
        return singleObserver;
    }
}
