package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

public class LifecycleObservableTransformer<T> implements ObservableTransformer<T, T> {

    private RxTerminatingLifecycleObserver observer;

    LifecycleObservableTransformer(final LifecycleOwner lifecycleOwner, final Lifecycle.State state) {
        this.observer = new RxTerminatingLifecycleObserver(lifecycleOwner, state);
    }

    @Override
    public ObservableSource<T> apply(final Observable<T> upstream) {
        this.observer.setDisposable(upstream.subscribe());
        return upstream;
    }
}