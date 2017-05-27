package com.waylonbrown.lifecycleawarerx.reactivetypes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * Wraps an {@link Observable} with it's {@link Observer}.
 */
public class ObservableWithObserver<T> implements BaseReactiveTypeWithObserver<Observable<T>, Observer<T>> {

    @Nullable private Observable<T> observable;
    @NonNull private final Observer<T> observer;

    public ObservableWithObserver(@NonNull Observer<T> observer) {
        this.observer = observer;
    }

    @Override
    public void subscribeWithObserver() {
        if (observable != null) {
            observable.subscribe(observer);
        }
    }

    @Override
    public void setReactiveType(Observable<T> observable) {
        this.observable = observable;
    }

    @NonNull
    @Override
    public Observable<T> getReactiveType() {
        return observable;
    }

    @NonNull
    @Override
    public Observer<T> getObserver() {
        return observer;
    }
}
