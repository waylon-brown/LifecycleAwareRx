package com.waylonbrown.lifecycleawarerx.reactivetypes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;

/**
 * Wraps an {@link Maybe} with it's {@link MaybeObserver}.
 */
public class MaybeWithObserver<T> implements BaseReactiveTypeWithObserver<Maybe<T>, MaybeObserver<T>> {

    @Nullable private Maybe<T> maybe;
    @NonNull private final MaybeObserver<T> observer;

    public MaybeWithObserver(@NonNull MaybeObserver<T> observer) {
        this.observer = observer;
    }

    @Override
    public void subscribeWithObserver() {
        if (maybe != null) {
            maybe.subscribe(observer);
        }
    }

    @Override
    public void setReactiveType(Maybe<T> maybe) {
        this.maybe = maybe;
    }
}
