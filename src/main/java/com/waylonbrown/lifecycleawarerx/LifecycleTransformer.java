package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import io.reactivex.SingleTransformer;

public class LifecycleTransformer {

    public static <T> SingleTransformer<T, T> disposeOnLifecycleEvent(LifecycleOwner lifecycleOwner,
                                                                  Lifecycle.State state) {
        return new LifecycleSingleTransformer(lifecycleOwner, state);
    }
}
