package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import io.reactivex.disposables.Disposable;

public class RxTerminatingLifecycleObserver implements LifecycleObserver {
    private final String TAG = RxTerminatingLifecycleObserver.class.getSimpleName();

    private final LifecycleOwner lifecycleOwner;
    private final Lifecycle.State terminalState;
    private Disposable disposable;

    public RxTerminatingLifecycleObserver(final LifecycleOwner lifecycleOwner, final Lifecycle.State terminalState) {
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

    public void setDisposable(Disposable disposable) {
        this.disposable = disposable;
        disposeIfReachedTerminalState();
    }

    private void disposeIfReachedTerminalState() {
        if (lifecycleOwner.getLifecycle().getCurrentState() == terminalState
                && disposable != null
                && !disposable.isDisposed()) {
            disposable.dispose();
            Log.i(TAG, "Disposed");
        }
    }
}
