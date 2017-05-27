package com.waylonbrown.lifecycleawarerx.reactivetypes;

/**
 * @param <R> base reactive type
 * @param <O> observer type
 */
public interface BaseReactiveTypeWithObserver<R, O> {
    
    R getReactiveType();
    
    O getObserver();
    
    void setReactiveType(R reactiveType);
    
    void subscribeWithObserver();
}
