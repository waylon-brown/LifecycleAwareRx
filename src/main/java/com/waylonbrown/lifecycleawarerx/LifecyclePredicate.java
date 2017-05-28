package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import io.reactivex.functions.Predicate;

public class LifecyclePredicate implements Predicate {

    private final LifecycleTransformer lifecycleTransformer;
    private LifecycleOwner lifecycleOwner;

    LifecyclePredicate(final LifecycleTransformer lifecycleTransformer, 
                       LifecycleOwner lifecycleOwner) {
        this.lifecycleTransformer = lifecycleTransformer;
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public boolean test(final Object o) throws Exception {
        boolean isDestroyed = lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED;
        if (isDestroyed) {
            // No memory leaks please
            this.lifecycleOwner = null;
            this.lifecycleTransformer.cleanup();
        }
        // If not destroyed, predicate is true and emits streams items as normal. Otherwise it ends.
        return !isDestroyed;
    }
}
