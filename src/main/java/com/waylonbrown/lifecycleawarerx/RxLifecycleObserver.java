package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.waylonbrown.lifecycleawarerx.reactivetypes.BaseReactiveTypeWithObserver;
import com.waylonbrown.lifecycleawarerx.reactivetypes.SingleWithObserver;
import com.waylonbrown.lifecycleawarerx.util.LifecycleUtil;

import io.reactivex.disposables.Disposable;

public class RxLifecycleObserver<T, R, O> implements LifecycleObserver {
    private final String TAG = RxLifecycleObserver.class.getSimpleName();

    /**
     * Since we're holding a reference to the LifecycleOwner, it's important that we remove this reference as soon
     * as it reaches a destroyed state to prevent a memory leak.
     */
    @NonNull private LifecycleOwner lifecycleOwner;
    
    // Used for destroying the stream
    @Nullable private Disposable disposable;
    
    // Used for starting the stream once LifecycleOwner is active
    @Nullable private BaseReactiveTypeWithObserver<R, O> baseReactiveType;
    private boolean subscribed = false;

    RxLifecycleObserver(@NonNull final LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    @SuppressWarnings("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onStateChange() {
        Log.i(TAG, "Lifecycle changed to " + lifecycleOwner.getLifecycle().getCurrentState().toString());
        handleCurrentLifecycleState();
    }

    void setDisposable(Disposable disposable) {
        this.disposable = disposable;
        handleCurrentLifecycleState();
    }

    public void setBaseReactiveType(final BaseReactiveTypeWithObserver<R, O> baseReactiveType) {
        this.baseReactiveType = baseReactiveType;
    }

    private void handleCurrentLifecycleState() {
        if (lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            endStreamAndCleanup();
        } else if (LifecycleUtil.isInActiveState(lifecycleOwner) && !subscribed && baseReactiveType != null) {
            Log.i(TAG, "Subscribing to observer.");
            
            // Subscribe to stream with observer since the LifecycleOwner is now active but wasn't previously
            ((SingleWithObserver<T>) baseReactiveType).getReactiveType().subscribe(((SingleWithObserver<T>) 
                baseReactiveType).getObserver());
            subscribed = true;
        }
    }

    private void endStreamAndCleanup() {
        Log.i(TAG, "LifecycleOwner is destroyed, disposing stream.");
        if (disposable != null) {
            disposable.dispose();
        }
        lifecycleOwner.getLifecycle().removeObserver(this);
        lifecycleOwner = null;  // No memory leaks please
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
