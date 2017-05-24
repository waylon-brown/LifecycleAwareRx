package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;

public class LifecycleSingleTransformer<T> implements SingleTransformer<T, T> {

    private final LifecycleOwner lifecycleOwner;
    private RxTerminatingLifecycleObserver observer;

    public LifecycleSingleTransformer(final LifecycleOwner lifecycleOwner, final Lifecycle.State state) {
        this.lifecycleOwner = lifecycleOwner;
        this.observer = new RxTerminatingLifecycleObserver(lifecycleOwner, state);
        lifecycleOwner.getLifecycle().addObserver(observer);
    }

    @Override
    public SingleSource<T> apply(final Single<T> upstream) {
        if (lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            upstream.subscribe().dispose();
        }
        this.observer.setDisposable(upstream.subscribe());
        return upstream;
    }
}