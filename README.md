[![](https://jitpack.io/v/WaylonBrown/LifecycleAwareRx.svg)](https://jitpack.io/#WaylonBrown/LifecycleAwareRx)

## Lifecycle-aware RxJava2 with Android's new `Lifecycle` API

With the introduction to [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/index.html), Google added `LiveData` to their Lifecycle component which is a very simple observable that is "lifecycle-aware". It takes advantage of a new API in Activities and Fragments, LifecycleOwner, which has lifecycle state callbacks and ensures that you only observe new values returned by the observable once the Activity/Fragment is active and destroys itself once the Activity/Fragment is destroyed automatically. *Goodbye memory leaks and prematurely accessed views.*

**LifecycleAwareRx** is a lightweight library that lets RxJava leverage the same API, making your reactive streams life-cycle aware with Android's **first-party** lifecycle API.

**The two main features you get out of using LifecycleAwareRx:**
1. As soon as your Activity/Fragment is destroyed, your RxJava2 stream ends and stops emitting items and the LifecycleOwner reference is removed ensuring you don't have any memory leaks or access to views from your stream after they've been destroyed.
2. Your stream won't subscribe to its Observer - which has the callbacks you care about to update the views - until the Activity/Fragment is active. The stream will still do its work beforehand, but it will cache any items that are emitted and only subscribe to the Observer once the ActivityFragment is active and its views are ready and emit each of the cached items in-order.

Right now this is with the use of Google's new `LifecycleActivity` and `LifecycleFragment`, but very soon they are going to bake the LifecycleOwner API directly into the support library's Activity/Fragment, meaning you'll be able to use this library with your current Activities and Fragments with no changes.

## How to use

To reiterate from above, for now you first need to have your Activities and Fragments that want to use this extend `LifecycleActivity` and `LifecycleFragment`.

Then it's as simple as

```Java
getMyObservable()
	.compose(LifecycleBinder.bind(this, new DisposableObserver<MyObject>() {
		@Override
		public void onNext(final <MyObject> myObject) {
			updateMyViewsWithData(myObject); // You can safely update your views here, knowing the Activity/Fragment isn't destroyed
		}
		
		@Override
		public void onError(final Throwable e) {
			updateMyViewsWithError(e); // Same here, except you need to do a state check here with Singles! See the note under "Singles are special" as to why.
		}

		@Override
		public void onComplete() {
		}
	}));
		
```

where 

* `getMyObservable()` could also instead be a `Single` or `Maybe`
* `this` is your Activity or Fragment
* `DisposableObserver` could instead be a regular `Observer`, or could otherwise be the correct type for your reactive type such as `SingleObserver`, `DisposableSingleObserver`, etc.

This automatically stops emitting items and throws away the LifecycleOwner (your Activity or Fragment) reference as soon as it hits `onDestroy()`. It also waits until your Activity/Fragment has its `onStart()` called before emitting any items, ensuring your views are ready to be updated. 

Because items are cached if they aren't yet ready to be emitted then are all emitted in-order once the onStart() is called, with Observables you can use `takeLast(1)` before you call `compose()` if you only want the last item cached to be emitted at that point instead of all items cached to be emitted.

## Singles are special
Singles can't be empty, they either represent a success or a failure. Because of this, if the onDestroy() is called before the Single emits its item, it will emit an `onError()` with a `NoSuchElementException()`. Because of this, you need to make sure to do the following check if you want to update your views in onError() if the stream is a Single (**Observables and Maybes don't need this check**).

```Java
// This is within your Single's Observer that is uses inside of the compose()
@Override
public void onError(final Throwable e) {
	if (getLifecycle().getCurrentState() != State.DESTROYED) {
		updateMyViewsWithError(e);
	}
}
```

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

## How's this different from RxLifecycle

I'm only adding this section because several have asked the same question :) I wouldn't have made this if it were the same thing, it fulfills a different but similar need.

1) The most important difference is that it uses Android's new first-party Lifecycle API. This means that you extend your Activity or Fragment as usual rather than extending a new class created just by this library (well, for now using Android's new LifecycleActivity and LifecycleFragment, but this functionality will soon be added straight into the support library's Activity/Fragment). Also, the complexity is far simpler than RxLifecycle since their API gives us observers for observing lifecycle state changes.
2) On top of just completing the stream once the Activity/Fragment is destroyed, it also takes care of deferring emitting items until the Activity/Fragment is active, as is done with Android's new `LiveData`.
