[![](https://jitpack.io/v/WaylonBrown/LifecycleAwareRx.svg)](https://jitpack.io/#WaylonBrown/LifecycleAwareRx)

## Lifecycle-aware RxJava2

With the introduction to [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/index.html), Google added `LiveData` to their Lifecycle component which is a very simple observable, but it is "lifecycle-aware" - it takes advantage of a new API in Activities and Fragments: LifecycleOwner, which has lifecycle state callbacks and ensures that you only observe new values returned by the observable once the Activity/Fragment is active, and destroys itself once the Activity/Fragment is destroyed automatically. *Goodbye memory leaks and prematurely accessed views.*

**LifecycleAwareRx** is a lightweight library that lets RxJava leverage the same API, making your reactive streams life-cycle aware with Android's first-party lifecycle API.

**The two main features you get out of using LifecycleAwareRx:**
1. Your RxJava2 stream is disposed of and the LifecycleOwner reference is removed as soon as your Activity/Fragment is destroyed, ensuring you don't have any memory leaks or access views after they've been destroyed.
2. Optionally, your stream won't subscribe to it's Observer - which has the callbacks you care about to update the views - until the Activity/Fragment is active. The stream will still do its work beforehand, but it will cache any items that are emitted and only subscribe to the Observer once the ActivityFragment is active and its views are ready and emit each of the cached items in-order.

Right now this is with the use of Google's new `LifecycleActivity` and `LifecycleFragment`, but very soon they are going to bake the LifecycleOwner API directly into the support library's Activity/Fragment, meaning you'll be able to use this library with your current Activities and Fragments with no changes.

## How to use

To reiterate from above, for now you need to have your Activities and Fragments that want to use this extend `LifecycleActivity` and `LifecycleFragment`. Then if you want the stream to be destroyed once the Activity/Fragment is destroyed, it's as simple as

```Java
getMyObservable()	// each of the other base reactive types are also supported (Single, Maybe, Completable)
  .compose(LifecycleBinder.disposeOnDestroy(this))
                ...
```

where `this` is your Activity or Fragment. This automatically disposes of the observable and throws away the LifecycleOwner (your Activity or Fragment) reference as soon as it hits `onDestroy()`. *Alternatively,* if you also would like to ensure that your views aren't accessed before the Activity/Fragment is active but still do all of the work that your stream needs to do beforehand, also pass in an Observer.

```Java
// This also supports passing in a DisposableObserver if you don't care about handling onSubscribe(), as well as
// using the observer types of the other base reactive types (SingleObserver, MaybeObserver, DisposableSingleObserver, etc.)
getMyObservable()
    .compose(LifecycleBinder.bindLifecycle(this, new Observer<Listing>() {
	@Override
	public void onSubscribe(final Disposable d) {
	}

	@Override
	public void onNext(final MyReturnedObject myReturnedObject) {
	    updateViews(myReturnedObject); // Feel secure knowing that this is only called if the Activity/Fragment is active
	}

	@Override
	public void onError(final Throwable e) {
	    showErrorView(e);
	}

	@Override
	public void onComplete() {
	}
    }));
```

Any items that are emitted by the stream before the Activity/Fragment is active are cached, and once the Activity/Fragment is active the stream subscribes to this Observer and emits each of the cached items in-order. You can also have the Observer only react to the latest item emitted using standard RxJava constructs.

## Add to your project
Add the jitpack repository if you haven't already to your *top-level project build.gradle.*

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Then add the dependency to your project's build.gradle.

```
dependencies {
  compile 'com.github.WaylonBrown:LifecycleAwareRx:0.2'	// Check the JitPack badge at the top of the README for the latest version.
}
```

## New library features

**I am very open to any new feature suggestions, so please add Github issues if there's something that should be added.**
