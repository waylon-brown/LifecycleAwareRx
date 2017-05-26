package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

public class LifecycleComposer {

    public static <T> LifecycleTransformer<T> bindLifeCycle(LifecycleOwner lifecycleOwner) {
        return disposeOnLifecycleEvent(lifecycleOwner, Lifecycle.State.DESTROYED);
    }

    public static <T> LifecycleTransformer<T> disposeOnLifecycleEvent(LifecycleOwner lifecycleOwner,
                                                                          Lifecycle.State state) {
        return new LifecycleTransformer<>(lifecycleOwner, state);
    }
}
