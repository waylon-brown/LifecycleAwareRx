package com.waylonbrown.lifecycleawarerx.util;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.Nullable;

public final class LifecycleUtil {
    
    // Can't initialize
    private LifecycleUtil() {}

    /**
     * A {@link LifecycleOwner} is considered active if it is either STARTED or RESUMED.
     * 
     * @param lifecycleOwner to check the state of.
     * @return whether the lifecycleOwner is active.
     */
    public static boolean isInActiveState(@Nullable LifecycleOwner lifecycleOwner) {
        return lifecycleOwner != null
            && lifecycleOwner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
    }
}
