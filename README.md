Readme is very much a work in progress at the moment.

## Lifecycle-aware RxJava

With the introduction to Android Architecture Components, Google added `LiveData` to their Lifecycle component which is a very simple observable, but takes advantage of a new API in Activities and Fragments: LifecycleOwner. 

Because of this, LiveData is lifecycle-aware, meaning that once the Activity/Fragment is destroyed, so is the LiveData automatically. Goodbye memory leaks.

This library lets RxJava leverage the same API, making your reactive streams life-cycle aware. Right now this is with the use of Google's new `LifecycleActivity` and `LifecycleFragment`, but very soon they are going to bake the LifecycleOwner API directly into the support library's Activity/Fragment, meaning using this library is and will be a piece of cake.

## Use

To reiterate from above, for now you need to have your Activities and Fragments that want to use this extend `LifecycleActivity` and `LifecycleFragment`. Then it's as simple as

```Java
viewModel.getMyObservable()
                .compose(LifecycleComposer.bindLifeCycle(this))
                ...
```

where `this` is your Activity or Fragment. This automatically disposes of the observable and throws away the LifecycleOwner (your Activity or Fragment) reference as soon as it hits `onDestroy()`. You can instead customize which state in the lifecycle you want it to terminate with

```Java
viewModel.getMyObservable()
                .compose(LifecycleComposer.disposeOnLifecycleEvent(this, Lifecycle.State.RESUMED))
                ...
```

Lastly, it also works with the other base reactive types.

```Java
// Single
viewModel.getMyObservable()
                .compose(LifecycleComposer.Single.bindLifeCycle(this))
                ...
         
// Completable
viewModel.getMyObservable()
                .compose(LifecycleComposer.Completable.bindLifeCycle(this))
                ...
         
// Maybe
viewModel.getMyObservable()
                .compose(LifecycleComposer.Maybe.bindLifeCycle(this))
                ...
```
