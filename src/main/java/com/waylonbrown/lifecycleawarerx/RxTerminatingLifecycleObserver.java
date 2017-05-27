package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.waylonbrown.lifecycleawarerx.util.LifecycleUtil;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

public class RxTerminatingLifecycleObserver<T> implements LifecycleObserver {
    private final String TAG = RxTerminatingLifecycleObserver.class.getSimpleName();

    /**
     * Since we're holding a reference to the LifecycleOwner, it's important that we remove this reference as soon
     * as it reaches a destroyed state to prevent a memory leak.
     */
    private LifecycleOwner lifecycleOwner;
    private final DisposableSingleObserver<T> disposableSingleObserver;
    private boolean subscribed = false;
    @Nullable private Single<T> single;

    RxTerminatingLifecycleObserver(@NonNull final LifecycleOwner lifecycleOwner, 
                                       final DisposableSingleObserver<T> disposableSingleObserver) {
        this.lifecycleOwner = lifecycleOwner;
        this.disposableSingleObserver = disposableSingleObserver;
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    @SuppressWarnings("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onStateChange() {
        Log.i(TAG, "Lifecycle changed to " + lifecycleOwner.getLifecycle().getCurrentState().toString());
        handleCurrentLifecycleState();
    }

    void setSingle(final Single<T> single) {
        this.single = single;
        handleCurrentLifecycleState();
    }

    private void handleCurrentLifecycleState() {
        if (lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            endStreamAndCleanup();
        } else if (LifecycleUtil.isInActiveState(lifecycleOwner) && !subscribed && single != null) {
            Log.i(TAG, "Subscribing to observer.");
            single.subscribeWith(disposableSingleObserver);
            subscribed = true;
        }
    }

    private void endStreamAndCleanup() {
        Log.i(TAG, "LifecycleOwner is destroyed, disposing stream.");
        if (single != null) {
            single.subscribe().dispose();
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

        RxTerminatingLifecycleObserver that = (RxTerminatingLifecycleObserver) o;

        return lifecycleOwner != null ? lifecycleOwner.equals(that.lifecycleOwner) : that.lifecycleOwner == null;
    }

    @Override
    public int hashCode() {
        return lifecycleOwner != null ? lifecycleOwner.hashCode() : 0;
    }
}
