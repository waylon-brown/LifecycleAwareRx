package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;

public class LifecycleMaybeTransformer<T> implements MaybeTransformer<T, T> {

    private RxTerminatingLifecycleObserver observer;

    public LifecycleMaybeTransformer(final LifecycleOwner lifecycleOwner, final Lifecycle.State state) {
        this.observer = new RxTerminatingLifecycleObserver(lifecycleOwner, state);
    }

    @Override
    public MaybeSource<T> apply(final Maybe<T> upstream) {
        this.observer.setDisposable(upstream.subscribe());
        return upstream;
    }
}