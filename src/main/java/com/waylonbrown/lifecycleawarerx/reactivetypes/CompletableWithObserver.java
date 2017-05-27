package com.waylonbrown.lifecycleawarerx.reactivetypes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;

/**
 * Wraps an {@link Maybe} with it's {@link MaybeObserver}.
 */
public class CompletableWithObserver implements BaseReactiveTypeWithObserver<Completable, CompletableObserver> {

    @Nullable private Completable completable;
    @NonNull private final CompletableObserver observer;

    public CompletableWithObserver(@NonNull CompletableObserver observer) {
        this.observer = observer;
    }

    @Override
    public void subscribeWithObserver() {
        if (completable != null) {
            completable.subscribe(observer);
        }
    }

    @Override
    public void setReactiveType(Completable maybe) {
        this.completable = maybe;
    }

    @NonNull
    @Override
    public Completable getReactiveType() {
        return completable;
    }

    @NonNull
    @Override
    public CompletableObserver getObserver() {
        return observer;
    }
}
