package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;

public class LifecycleCompletableTransformer implements CompletableTransformer {

    private RxTerminatingLifecycleObserver observer;

    public LifecycleCompletableTransformer(final LifecycleOwner lifecycleOwner, final Lifecycle.State state) {
        this.observer = new RxTerminatingLifecycleObserver(lifecycleOwner, state);
    }

    @Override
    public CompletableSource apply(final Completable upstream) {
        this.observer.setDisposable(upstream.subscribe());
        return upstream;
    }
}