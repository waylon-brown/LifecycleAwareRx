package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.disposables.Disposable;

public class LifecycleTransformer<T> implements ObservableTransformer<T, T>,
		SingleTransformer<T, T>,
		MaybeTransformer<T, T>,
		CompletableTransformer {

	private final String TAG = LifecycleTransformer.class.getSimpleName();

	@Nullable
	private RxTerminatingLifecycleObserver observer;

	LifecycleTransformer(@NonNull final LifecycleOwner lifecycleOwner, final Lifecycle.State state) {
		if (lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
			return;
		}
		this.observer = new RxTerminatingLifecycleObserver(lifecycleOwner, state);
	}

	@Override
	public ObservableSource<T> apply(final Observable<T> upstream) {
		setDisposableToObserver(upstream.subscribe());
		return upstream;
	}

	@Override
	public SingleSource<T> apply(Single<T> upstream) {
		setDisposableToObserver(upstream.subscribe());
		return upstream;
	}

	@Override
	public MaybeSource<T> apply(Maybe<T> upstream) {
		setDisposableToObserver(upstream.subscribe());
		return upstream;
	}

	@Override
	public CompletableSource apply(Completable upstream) {
		setDisposableToObserver(upstream.subscribe());
		return upstream;
	}

	private void setDisposableToObserver(Disposable disposable) {
		if (this.observer != null) {
			this.observer.setDisposable(disposable);
		} else {
			// Is null because it the LifecycleOwner is in destroyed state
			disposable.dispose();
			Log.i(TAG, "Disposed stream because it was already destroyed.");
		}
	}
}