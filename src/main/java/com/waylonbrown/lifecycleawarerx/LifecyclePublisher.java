package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.LifecycleOwner;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class LifecyclePublisher implements Publisher {

    private final LifecycleTransformer lifecycleTransformer;
    private final LifecycleOwner lifecycleOwner;

    public LifecyclePublisher(final LifecycleTransformer lifecycleTransformer,
                              final LifecycleOwner lifecycleOwner) {
        this.lifecycleTransformer = lifecycleTransformer;
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public void subscribe(final Subscriber s) {
        // TODO: make this happen on onDestroy
        s.onNext(new Object());
    }
}
