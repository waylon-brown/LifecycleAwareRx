package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.waylonbrown.lifecycleawarerx.reactivetypes.BaseReactiveTypeWithObserver;

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

/**
 * Transformer that is used by the compose() method of your stream.
 * 
 * @param <T> stream inner type (what you want returned in your subscription)
 * @param <R> reactive type
 * @param <O> observer type
 */
public class LifecycleTransformer<T, R, O> implements ObservableTransformer<T, T>,
		SingleTransformer<T, T>,
		MaybeTransformer<T, T>,
		CompletableTransformer {

	@Nullable
	private BaseReactiveTypeWithObserver<R, O> baseReactiveType;
	@Nullable
	private RxLifecycleObserver<R, O> lifecycleObserver;

	LifecycleTransformer(@NonNull final LifecycleOwner lifecycleOwner,
						 @Nullable final BaseReactiveTypeWithObserver<R, O> baseReactiveType) {
		// We're also handling delaying subscription until the LifecycleOwner is active
		if (baseReactiveType != null) {
			this.baseReactiveType = baseReactiveType;
			this.lifecycleObserver = new RxLifecycleObserver<>(lifecycleOwner);
		}
	}

	@Override
	public ObservableSource<T> apply(final Observable<T> upstream) {
		if (lifecycleObserver != null
			&& baseReactiveType != null) {

			// Replay emitted values to late subscriber
			upstream.cache();
			setReactiveType((R)upstream);
		}
		return upstream;
	}

	@Override
	public SingleSource<T> apply(Single<T> upstream) {
		if(lifecycleObserver != null
			&& baseReactiveType != null) {
			
			// Replay emitted values to late subscriber
			upstream.cache();
			setReactiveType((R)upstream);
		}
		return upstream;
	}

	@Override
	public MaybeSource<T> apply(Maybe<T> upstream) {
		if (lifecycleObserver != null
			&& baseReactiveType != null) {

			// Replay emitted values to late subscriber
			upstream.cache();
			setReactiveType((R)upstream);
		}
		return upstream;
	}

	@Override
	public CompletableSource apply(Completable upstream) {
		if (lifecycleObserver != null
			&& baseReactiveType != null) {
			
			// Can't cache a Completable
			setReactiveType((R)upstream);
		}
		return upstream;
	}

	private void setReactiveType(final R upstream) {
		baseReactiveType.setReactiveType(upstream);
		lifecycleObserver.setBaseReactiveType(baseReactiveType);
	}
}