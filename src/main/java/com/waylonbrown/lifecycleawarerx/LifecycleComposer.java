package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import io.reactivex.SingleTransformer;

public class LifecycleComposer {

    public static <T> SingleTransformer<T, T> bindLifeCycle(LifecycleOwner lifecycleOwner) {
        return disposeOnLifecycleEvent(lifecycleOwner, Lifecycle.State.DESTROYED);
    }

    public static <T> SingleTransformer<T, T> disposeOnLifecycleEvent(LifecycleOwner lifecycleOwner,
                                                                  Lifecycle.State state) {
        return new LifecycleSingleTransformer(lifecycleOwner, state);
    }
}
