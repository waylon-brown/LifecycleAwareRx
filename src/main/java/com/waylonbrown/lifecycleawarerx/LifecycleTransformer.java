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
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.disposables.Disposable;

public class LifecycleTransformer<T> implements ObservableTransformer<T, T>,
		SingleTransformer<T, T>,
		MaybeTransformer<T, T>,
		CompletableTransformer {

	private final String TAG = LifecycleTransformer.class.getSimpleName();
	
	@Nullable
	private SingleObserver<T> singleObserver;

	@Nullable
	private RxLifecycleObserver observer;

	LifecycleTransformer(@NonNull final LifecycleOwner lifecycleOwner, 
						 @Nullable final SingleObserver<T> singleObserver) {
		if (lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
			return;
		}
		this.singleObserver = singleObserver;
		this.observer = new RxLifecycleObserver(lifecycleOwner);
	}

	@Override
	public ObservableSource<T> apply(final Observable<T> upstream) {
		setDisposableAndReturnIfDisposed(upstream.subscribe());
		return upstream;
	}

	@Override
	public SingleSource<T> apply(Single<T> upstream) {
		if(!setDisposableAndReturnIfDisposed(upstream.subscribe()) 
			&& singleObserver != null) {
			this.observer.setBaseReactiveType(new SingleWrapper<>(upstream, singleObserver));
		}
		return upstream;
	}

	@Override
	public MaybeSource<T> apply(Maybe<T> upstream) {
		setDisposableAndReturnIfDisposed(upstream.subscribe());
		return upstream;
	}

	@Override
	public CompletableSource apply(Completable upstream) {
		setDisposableAndReturnIfDisposed(upstream.subscribe());
		return upstream;
	}

	/**
	 * @param disposable
	 * @return true if the disposable was disposed because LifecycleOwner is already destroyed.
	 */
	private boolean setDisposableAndReturnIfDisposed(Disposable disposable) {
		if (this.observer != null) {
			this.observer.setDisposable(disposable);
			return false;
		}
		// Is null because the LifecycleOwner is in destroyed state
		disposable.dispose();
		singleObserver = null;
		Log.i(TAG, "Disposed stream because it was already destroyed.");
		return true;
	}
}