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
 * Contains the methods to be statically called within your RxJava2 stream to
 * 
 *      1) Ensure your stream completes and stops emitting items once your {@link LifecycleOwner} (your Activity or 
 * Fragment) is destroyed. See {@link #notDestroyed(LifecycleOwner)}.
 * 
 *      2) Ensure your stream doesn't emit items until the {@link LifecycleOwner} is active. Items received before the 
 * LifecycleOwner is active are cached, then emitted as soon as it is active. For Observables, you can use 
 * takeLast(1) before calling this to only have the latest item emitted as soon as the LifecycleOwner is active. 
 * See {@link #subscribeWhenReady(LifecycleOwner, Observer)}.
 * 
 * An example of a stream taking advantage of a full lifecycle binding:
 * 
 * getMyObservable()
 *      .takeWhile(LifecycleBinder.notDestroyed(this))
 *      .compose(LifecycleBinder.subscribeWhenReady(new DisposableObserver<MyObject>() {
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
     * Pass into filter() to tell your stream to stop emitting items once the {@link LifecycleOwner} (your 
     * Activity/Fragment) is destroyed. 
     * 
     * For Observables only, you can instead pass this into takeWhile() to end the stream as soon as the 
     * LifecycleOwner is destroyed and the next item is emitted. This is sometimes preferred with Observables so that 
     * your stream can stop doing work rather than filter out each of the future items of the stream. This calls 
     * onComplete() unlike filter() so be sure to not access any views in onComplete() if using takeWhile().
     * 
     * This only handles disposing of the stream and removing {@link LifecycleOwner} references once the lifecycle
     * is destroyed. If you also want to delay subscription until the lifecycle is active to ensure you are only 
     * accessing your views when they are ready, also use one of the subscribeWhenReady() methods.
     * 
     * @param lifecycleOwner which is your {@link android.arch.lifecycle.LifecycleActivity} or 
     *      {@link android.arch.lifecycle.LifecycleFragment}, which will eventually be compatible with the support 
     *      library Activity and Fragment.
     * @return the Predicate to be used in filter() or takeWhile() of your stream.
     */
    public static <T> LifecyclePredicate<T> notDestroyed(@NonNull LifecycleOwner lifecycleOwner) {
        return new LifecyclePredicate<>(lifecycleOwner);
    }
    
    /**
     * Each of the subscribeWhenReady() methods take care of subscribing to the observer only once your lifecycle is 
     * actually active, thereby ensuring your views are only accessed once they are ready.
     * 
     * The stream is cached, meaning that any items that are emitted before it is actually subscribed to are stored, 
     * then emitted in-order at the time of subscription.
     * 
     * For Observables, you can use takeLast(1) before calling this to only have the latest item emitted as soon as 
     * the LifecycleOwner is active.
     * 
     * @param lifecycleOwner which is your {@link android.arch.lifecycle.LifecycleActivity} or 
     *      {@link android.arch.lifecycle.LifecycleFragment}, which will eventually be compatible with the support 
     *      library Activity and Fragment.
     * @param singleObserver or other observers of these methods. You can also pass in 
     *      {@link io.reactivex.observers.DisposableObserver} (or the related Disposable methods for each base 
     *      reactive type) if you don't care about handling the onSubscribe().
     * @return the Transformer to be used in compose() of your stream.
     */
    public static <T> SingleTransformer<T, T> subscribeWhenReady(@NonNull LifecycleOwner lifecycleOwner,
                                                                 @NonNull SingleObserver<T> singleObserver) {
        return new LifecycleTransformer<>(lifecycleOwner, new SingleWithObserver<>(singleObserver));
    }

    public static <T> ObservableTransformer<T, T> subscribeWhenReady(@NonNull LifecycleOwner lifecycleOwner,
                                                                     @NonNull Observer<T> observer) {
        return new LifecycleTransformer<>(lifecycleOwner, new ObservableWithObserver<>(observer));
    }

    public static <T> MaybeTransformer<T, T> subscribeWhenReady(@NonNull LifecycleOwner lifecycleOwner,
                                                                @NonNull MaybeObserver<T> observer) {
        return new LifecycleTransformer<>(lifecycleOwner, new MaybeWithObserver<>(observer));
    }
}
