package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.functions.Predicate;

/**
 * Decides whether to keep emitting items based on if the {@link LifecycleOwner} is destroyed or not.
 */
public class LifecyclePredicate<T> implements Predicate<T> {

    @Nullable
    private LifecycleOwner lifecycleOwner;

    LifecyclePredicate(@NonNull LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public boolean test(final T object) throws Exception {
        // We've already removed the reference, don't emit anymore items.
        if (lifecycleOwner == null) {
            return false;
        }
        
        boolean isDestroyed = lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED;
        if (isDestroyed) {
            // No memory leaks please
            this.lifecycleOwner = null;
        }
        // If not destroyed, predicate is true and emits streams items as normal. Otherwise it ends.
        return !isDestroyed;
    }
}
