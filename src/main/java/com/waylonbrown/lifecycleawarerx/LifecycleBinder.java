package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import com.waylonbrown.lifecycleawarerx.reactivetypes.MaybeWithObserver;
import com.waylonbrown.lifecycleawarerx.reactivetypes.ObservableWithObserver;
import com.waylonbrown.lifecycleawarerx.reactivetypes.SingleWithObserver;

import io.reactivex.MaybeObserver;
import io.reactivex.MaybeTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.SingleTransformer;

/**
 * Contains the methods for each base reactive type to be statically called within your RxJava2 stream to
 * 
 *      1) Ensure your stream completes and stops emitting items once your {@link LifecycleOwner} (your Activity or 
 * Fragment) is destroyed.
 * 
 *      2) Ensure your stream doesn't emit items until the {@link LifecycleOwner} is active. Items received before the 
 * LifecycleOwner is active are cached, then emitted as soon as it is active. For Observables, you can use 
 * takeLast(1) before calling this to only have the latest item emitted as soon as the LifecycleOwner is active.
 * 
 * NOTE WHEN USING THIS WITH SINGLES: When using this with a Single, if the LifecycleOwner is destroyed before it's 
 * ever ready (at the point of starting the stream), it will emit a NoSuchElementException since a Single can't be 
 * empty, so there is a chance the onError is called after onDestroy() when using Single(). This means you need to 
 * make sure you don't touch your views when using a Single in onError unless you check to make sure the 
 * Activity/Fragment isn't destroyed! A simple if(getLifecycle().getCurrentState != State.DESTROYED) before accessing 
 * your views there will suffice.
 * 
 * An example of a stream using this API:
 * 
 * getMyObservable()
 *      .compose(LifecycleBinder.bind(new DisposableObserver<MyObject>() {
 *          @Override
 *          public void onNext(final MyObject myObject) {
 *              updateMyViews(myObject); // This can be called safely here knowing your lifecycle is active and not 
 *                                       // destroyed.
 *          }
 *          
 *          @Override
 *          public void onError(final Throwable e) {
 *          }
 *          
 *          @Override
 *          public void onComplete() {
 *          }
 *      }));
 */
public class LifecycleBinder {
    
    /**
     * @param lifecycleOwner which is your {@link android.arch.lifecycle.LifecycleActivity} or 
     *      {@link android.arch.lifecycle.LifecycleFragment}, which will eventually be compatible with the support 
     *      library Activity and Fragment.
     * @param singleObserver or other observers of these methods. You can also pass in 
     *      {@link io.reactivex.observers.DisposableObserver} (or the related Disposable methods for each base 
     *      reactive type) if you don't care about handling the onSubscribe().
     * @return the Transformer to be used in compose() of your stream.
     */
    public static <T> SingleTransformer<T, T> bind(@NonNull LifecycleOwner lifecycleOwner,
                                                   @NonNull SingleObserver<T> singleObserver) {
        return new LifecycleTransformer<>(lifecycleOwner, new SingleWithObserver<>(singleObserver));
    }

    public static <T> ObservableTransformer<T, T> bind(@NonNull LifecycleOwner lifecycleOwner,
                                                       @NonNull Observer<T> observer) {
        return new LifecycleTransformer<>(lifecycleOwner, new ObservableWithObserver<>(observer));
    }

    public static <T> MaybeTransformer<T, T> bind(@NonNull LifecycleOwner lifecycleOwner,
                                                  @NonNull MaybeObserver<T> observer) {
        return new LifecycleTransformer<>(lifecycleOwner, new MaybeWithObserver<>(observer));
    }
}
