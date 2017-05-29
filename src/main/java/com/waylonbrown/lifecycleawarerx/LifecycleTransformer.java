package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import com.waylonbrown.lifecycleawarerx.reactivetypes.BaseReactiveTypeWithObserver;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;

/**
 * Transformer that is used by the compose() method of your stream to cache emitted items and subscribe to them once 
 * the {@link LifecycleOwner} is active.
 * 
 * @param <T> stream inner type (what you want returned in your subscription)
 * @param <R> reactive type
 * @param <O> observer type
 */
class LifecycleTransformer<T, R, O> implements ObservableTransformer<T, T>,
		SingleTransformer<T, T>,
		MaybeTransformer<T, T> {

	@NonNull
	private final BaseReactiveTypeWithObserver<R, O> baseReactiveType;
	@NonNull
	private final RxLifecycleObserver<R, O> lifecycleObserver;

	LifecycleTransformer(@NonNull final LifecycleOwner lifecycleOwner,
						 @NonNull final BaseReactiveTypeWithObserver<R, O> baseReactiveType) {
		this.baseReactiveType = baseReactiveType;
		this.lifecycleObserver = new RxLifecycleObserver<>(lifecycleOwner);
	}

	@Override
	public ObservableSource<T> apply(final Observable<T> upstream) {
		// Replay emitted values to late subscriber
		upstream.cache();
		setReactiveType((R)upstream);
		
		return upstream;
	}

	@Override
	public SingleSource<T> apply(Single<T> upstream) {
		// Replay emitted values to late subscriber
		upstream.cache();
		setReactiveType((R)upstream);
		
		return upstream;
	}

	@Override
	public MaybeSource<T> apply(Maybe<T> upstream) {
		// Replay emitted values to late subscriber
		upstream.cache();
		setReactiveType((R)upstream);
		
		return upstream;
	}

	private void setReactiveType(final R upstream) {
		baseReactiveType.setReactiveType(upstream);
		lifecycleObserver.setBaseReactiveType(baseReactiveType);
	}
}