package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.waylonbrown.lifecycleawarerx.util.LifecycleUtil;

import io.reactivex.disposables.Disposable;

public class RxTerminatingLifecycleObserver implements LifecycleObserver {
    private final String TAG = RxTerminatingLifecycleObserver.class.getSimpleName();

    /**
     * Since we're holding a reference to the LifecycleOwner, it's important that we remove this reference as soon
     * as it reaches a destroyed state to prevent a memory leak.
     */
    private LifecycleOwner lifecycleOwner;
    @Nullable
    private Disposable disposable;
    private boolean streamIsPaused = false;

    RxTerminatingLifecycleObserver(@NonNull final LifecycleOwner lifecycleOwner) {
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

    private void handleCurrentLifecycleState() {
        if (lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            // Should be destroyed
            endStream();
        } else if (!LifecycleUtil.isInActiveState(lifecycleOwner) && !streamIsPaused) {
            // Should pause the stream as the LifecycleOwner isn't yet ready for it to emit items
            pauseStream();
        } else if (streamIsPaused) {
            // Need to restart a stream if it was paused
            continueStream();
        }
    }

    private void endStream() {
        Log.i(TAG, "LifecycleOwner is destroyed, disposing stream.");
        if (disposable != null) {
            disposable.dispose();
        }
        lifecycleOwner.getLifecycle().removeObserver(this);
        lifecycleOwner = null;  // No memory leaks please
    }

    private void pauseStream() {
        Log.i(TAG, "LifecycleOwner isn't yet active, pausing stream.");
        streamIsPaused = true;
    }

    private void continueStream() {
        Log.i(TAG, "LifecycleOwner was inactive but is currently active, continuing stream.");
        streamIsPaused = false;
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
