package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.LifecycleOwner;

public class LifecycleComposer {

    public static <T> LifecycleTransformer<T> bindLifeCycle(LifecycleOwner lifecycleOwner) {
        return new LifecycleTransformer<>(lifecycleOwner);
    }
}
