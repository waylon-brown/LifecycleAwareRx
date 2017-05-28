package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import com.waylonbrown.lifecycleawarerx.reactivetypes.CompletableWithObserver;
import com.waylonbrown.lifecycleawarerx.reactivetypes.MaybeWithObserver;
import com.waylonbrown.lifecycleawarerx.reactivetypes.ObservableWithObserver;
import com.waylonbrown.lifecycleawarerx.reactivetypes.SingleWithObserver;

import java.util.concurrent.TimeUnit;

import io.reactivex.CompletableObserver;
import io.reactivex.CompletableTransformer;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.SingleTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;

/**
 * Contains the methods to be statically called within your RxJava2 stream using compose().
 */
public class LifecycleBinder {

    /**
     * Only handles disposing of the stream and removing {@link LifecycleOwner} references once the lifecycle
     * is destroyed. If you also want to delay subscription until the lifecycle is active to ensure you are only 
     * accessing your views when they are ready, instead use one of the bindLifecycle() methods.
     * 
     * @param lifecycleOwner which is your {@link android.arch.lifecycle.LifecycleActivity} or 
     *      {@link android.arch.lifecycle.LifecycleFragment}, which will eventually be compatible with the support 
     *      library Activity and Fragment.
     * @return the transformer to be used in compose() of your stream.
     */
    public static LifecycleTransformer disposeIfDestroyed(LifecycleOwner lifecycleOwner) {
        return new LifecycleTransformer(lifecycleOwner, null);
    }

    //TODO: move this into transformer so all done with compose
//    public static Predicate disposeIfDestroyed(LifecycleOwner lifecycleOwner) {
//        return new LifecyclePredicate(lifecycleOwner);
//    }
    
    /**
     * Each of the bindLifecycle() methods take care of subscribing to the observer only once your lifecycle is 
     * actually active, thereby ensuring your views are only accessed once they are ready, as well as disposing of the 
     * stream and removing the {@link LifecycleOwner} reference once the lifecycle is destroyed.
     * 
     * The stream is cached, meaning that any items that are emitted before it is actually subscribed to are stored, 
     * then emitted in-order at the time of subscription.
     * 
     * @param lifecycleOwner which is your {@link android.arch.lifecycle.LifecycleActivity} or 
     *      {@link android.arch.lifecycle.LifecycleFragment}, which will eventually be compatible with the support 
     *      library Activity and Fragment.
     * @param singleObserver or other observers of these methods. You can also pass in 
     *      {@link io.reactivex.observers.DisposableObserver} (or the related Disposable methods for each base 
     *      reactive type) if you don't care about handling the onSubscribe().
     * @return the transformer to be used in compose() of your stream.
     */
    public static <T> SingleTransformer<T, T> bindLifecycle(LifecycleOwner lifecycleOwner,
                                                            SingleObserver<T> singleObserver) {
        return new LifecycleTransformer<>(lifecycleOwner, new SingleWithObserver<>(singleObserver));
    }

    public static <T> ObservableTransformer<T, T> bindLifecycle(LifecycleOwner lifecycleOwner,
                                                                Observer<T> observer) {
        return new LifecycleTransformer<>(lifecycleOwner, new ObservableWithObserver<>(observer));
    }

    public static <T> MaybeTransformer<T, T> bindLifecycle(LifecycleOwner lifecycleOwner,
                                                           MaybeObserver<T> observer) {
        return new LifecycleTransformer<>(lifecycleOwner, new MaybeWithObserver<>(observer));
    }

    public static <T> CompletableTransformer bindLifecycle(LifecycleOwner lifecycleOwner,
                                                           CompletableObserver observer) {
        return new LifecycleTransformer<>(lifecycleOwner, new CompletableWithObserver(observer));
    }
}
