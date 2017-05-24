package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;

public class LifecycleSingleTransformer<T> implements SingleTransformer<T, T> {

    private RxTerminatingLifecycleObserver observer;

    public LifecycleSingleTransformer(final LifecycleOwner lifecycleOwner, final Lifecycle.State state) {
        this.observer = new RxTerminatingLifecycleObserver(lifecycleOwner, state);
    }

    @Override
    public SingleSource<T> apply(final Single<T> upstream) {
        this.observer.setDisposable(upstream.subscribe());
        return upstream;
    }
}