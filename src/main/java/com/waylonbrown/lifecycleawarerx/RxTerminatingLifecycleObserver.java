package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import io.reactivex.disposables.Disposable;

public class RxTerminatingLifecycleObserver implements LifecycleObserver {
    private final String TAG = RxTerminatingLifecycleObserver.class.getSimpleName();

    private Lifecycle.State terminalState;
    private LifecycleOwner lifecycleOwner;
    private Disposable disposable;

    RxTerminatingLifecycleObserver(final LifecycleOwner lifecycleOwner, final Lifecycle.State terminalState) {
        this.lifecycleOwner = lifecycleOwner;
        this.terminalState = terminalState;
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    @SuppressWarnings("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onStateChange() {
        Log.i(TAG, "Lifecycle changed to " + lifecycleOwner.getLifecycle().getCurrentState().toString());
        disposeIfReachedTerminalState();
    }

    void setDisposable(Disposable disposable) {
        this.disposable = disposable;
        disposeIfReachedTerminalState();
    }

    private void disposeIfReachedTerminalState() {
        if (lifecycleOwner.getLifecycle().getCurrentState() == terminalState
                && disposable != null
                && !disposable.isDisposed()) {
            disposable.dispose();
            lifecycleOwner.getLifecycle().removeObserver(this);
            lifecycleOwner = null;  // No memory leaks please
            Log.i(TAG, "Disposed stream because reached terminal state.");
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

        RxTerminatingLifecycleObserver that = (RxTerminatingLifecycleObserver) o;

        return lifecycleOwner != null ? lifecycleOwner.equals(that.lifecycleOwner) : that.lifecycleOwner == null;
    }

    @Override
    public int hashCode() {
        return lifecycleOwner != null ? lifecycleOwner.hashCode() : 0;
    }
}
