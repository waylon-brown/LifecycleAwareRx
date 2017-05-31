package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.functions.Predicate;

/**
 * Decides whether to keep emitting items based on if the {@link LifecycleOwner} is destroyed or not.
 * 
 * Implements {@link LifecycleObserver} just so that it can remove the {@link LifecycleOwner} reference once it hits 
 * the destroyed state.
 */
public class LifecyclePredicate<T> implements Predicate<T>, LifecycleObserver {

    @Nullable
    private LifecycleOwner lifecycleOwner;

    LifecyclePredicate(@NonNull LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        this.lifecycleOwner.getLifecycle().addObserver(this);
    }

    @Override
    public boolean test(final T object) throws Exception {
        // We've already removed the reference, don't emit anymore items.
        if (lifecycleOwner == null) {
            return false;
        }
        
        boolean isDestroyed = lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED;
        if (isDestroyed) {
            // This should have been handled in handleLifecycleEvent() at this point, but just being safe to not have
            // memory leaks.
            lifecycleOwner = null;
        }
        // If not destroyed, predicate is true and emits streams items as normal. Otherwise it ends.
        return !isDestroyed;
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onStateChange() {
        if (lifecycleOwner != null && lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            // No memory leaks please
            lifecycleOwner = null;
        }
    }
}
