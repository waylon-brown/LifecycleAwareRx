package com.waylonbrown.lifecycleawarerx.util;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

public final class LifecycleUtil {
    
    // Can't initialize
    private LifecycleUtil() {}

    /**
     * A {@link LifecycleOwner} is considered active if it is either STARTED or RESUMED.
     * 
     * @param lifecycleOwner
     * @return
     */
    public static boolean isInActiveState(LifecycleOwner lifecycleOwner) {
        return lifecycleOwner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
    }
}
