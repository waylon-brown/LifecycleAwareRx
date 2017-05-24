package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class RxTerminatingLifecycleObserver implements LifecycleObserver {
    private final LifecycleOwner lifecycleOwner;
    private final Lifecycle.State terminalState;
    private Disposable disposable;

    public RxTerminatingLifecycleObserver(final LifecycleOwner lifecycleOwner, final Lifecycle.State terminalState) {
        this.lifecycleOwner = lifecycleOwner;
        this.terminalState = terminalState;
    }

    @SuppressWarnings("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onStateChange() {
        Timber.i("Lifecycle changed to " + lifecycleOwner.getLifecycle().getCurrentState().toString());
        if (lifecycleOwner.getLifecycle().getCurrentState() == terminalState
            && disposable != null
            && !disposable.isDisposed()) {
            disposable.dispose();
            Timber.i("Disposed");
        }
    }
    
    public void setDisposable(Disposable disposable) {
        this.disposable = disposable;
    }
}
