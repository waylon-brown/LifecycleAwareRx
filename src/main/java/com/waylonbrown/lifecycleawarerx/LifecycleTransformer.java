package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

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

public class LifecycleTransformer<T> implements ObservableTransformer<T, T>,
		SingleTransformer<T, T>,
		MaybeTransformer<T, T>,
		CompletableTransformer {

	private RxTerminatingLifecycleObserver observer;

	LifecycleTransformer(@NonNull final LifecycleOwner lifecycleOwner, final Lifecycle.State state) {
		this.observer = new RxTerminatingLifecycleObserver(lifecycleOwner, state);
	}

	@Override
	public ObservableSource<T> apply(final Observable<T> upstream) {
		this.observer.setDisposable(upstream.subscribe());
		return upstream;
	}

	@Override
	public SingleSource<T> apply(Single<T> upstream) {
		this.observer.setDisposable(upstream.subscribe());
		return upstream;
	}

	@Override
	public MaybeSource<T> apply(Maybe<T> upstream) {
		this.observer.setDisposable(upstream.subscribe());
		return upstream;
	}

	@Override
	public CompletableSource apply(Completable upstream) {
		this.observer.setDisposable(upstream.subscribe());
		return upstream;
	}
}