package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.waylonbrown.lifecycleawarerx.reactivetypes.BaseReactiveTypeWithObserver;
import com.waylonbrown.lifecycleawarerx.util.LifecycleUtil;

/**
 * Observes state changes that happen to the {@link LifecycleOwner}, and subscribes to the stream once the lifecycle is 
 * active.
 * 
 * @param <R>
 * @param <O>
 */
public class RxLifecycleObserver<R, O> implements LifecycleObserver {
    /**
     * Since we're holding a reference to the LifecycleOwner, it's important that we remove this reference as soon
     * as it reaches a destroyed state to prevent a memory leak. Not using @NonNull since it is to later be set to null.
     */
    @Nullable // Since it's later set to null
    private LifecycleOwner lifecycleOwner;
    
    @Nullable
    private BaseReactiveTypeWithObserver<R, O> baseReactiveType;
    private boolean subscribed = false;

    RxLifecycleObserver(@NonNull final LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    @SuppressWarnings("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onStateChange() {
        handleCurrentLifecycleState();
    }

    void setBaseReactiveType(@NonNull final BaseReactiveTypeWithObserver<R, O> baseReactiveType) {
        this.baseReactiveType = baseReactiveType;
        handleCurrentLifecycleState();
    }
    
    /**
     * Decides whether the stream needs to be destroyed or subscribed to.
     */
    private void handleCurrentLifecycleState() {
        if (lifecycleOwner != null &&
            lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {

            // No memory leaks please
            this.lifecycleOwner = null;
            this.baseReactiveType = null;
        } else if (LifecycleUtil.isInActiveState(lifecycleOwner) 
            && !subscribed 
            && baseReactiveType != null) {
            
            // Subscribe to stream with observer since the LifecycleOwner is now active but wasn't previously
            baseReactiveType.subscribeWithObserver();

            subscribed = true;
        }
    }

    /**
     * We're overriding the equals() and hashCode() so that when
     * {@link android.arch.lifecycle.LifecycleRegistry#addObserver(LifecycleObserver)}
     * works its magic, it will replace a previously set observer from this class that has the
     * same {@link LifecycleOwner}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RxLifecycleObserver that = (RxLifecycleObserver) o;

        return lifecycleOwner != null ? lifecycleOwner.equals(that.lifecycleOwner) : that.lifecycleOwner == null;
    }

    @Override
    public int hashCode() {
        return lifecycleOwner != null ? lifecycleOwner.hashCode() : 0;
    }
}
