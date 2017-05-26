package com.waylonbrown.lifecycleawarerx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

public class LifecycleBinder {

    public static <T> LifecycleObservableTransformer<T> bindLifeCycle(LifecycleOwner lifecycleOwner) {
        return disposeOnLifecycleEvent(lifecycleOwner, Lifecycle.State.DESTROYED);
    }

    public static <T> LifecycleObservableTransformer<T> disposeOnLifecycleEvent(LifecycleOwner lifecycleOwner,
                                                                          Lifecycle.State state) {
        return new LifecycleObservableTransformer(lifecycleOwner, state);
    }

    public static class Single {

        public static <T> LifecycleSingleTransformer<T> bindLifeCycle(LifecycleOwner lifecycleOwner) {
            return disposeOnLifecycleEvent(lifecycleOwner, Lifecycle.State.DESTROYED);
        }

        public static <T> LifecycleSingleTransformer<T> disposeOnLifecycleEvent(LifecycleOwner lifecycleOwner,
                                                                                      Lifecycle.State state) {
            return new LifecycleSingleTransformer(lifecycleOwner, state);
        }
    }

    /**
     * Doesn't use generics.
     */
    public static class Completable {

        public static LifecycleCompletableTransformer bindLifeCycle(LifecycleOwner lifecycleOwner) {
            return disposeOnLifecycleEvent(lifecycleOwner, Lifecycle.State.DESTROYED);
        }

        public static LifecycleCompletableTransformer disposeOnLifecycleEvent(LifecycleOwner lifecycleOwner,
                                                                                Lifecycle.State state) {
            return new LifecycleCompletableTransformer(lifecycleOwner, state);
        }
    }

    public static class Maybe {

        public static <T> LifecycleMaybeTransformer<T> bindLifeCycle(LifecycleOwner lifecycleOwner) {
            return disposeOnLifecycleEvent(lifecycleOwner, Lifecycle.State.DESTROYED);
        }

        public static <T> LifecycleMaybeTransformer<T> disposeOnLifecycleEvent(LifecycleOwner lifecycleOwner,
                                                                                Lifecycle.State state) {
            return new LifecycleMaybeTransformer(lifecycleOwner, state);
        }
    }
}
