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

#### Make your stream end once the Activity/Fragment is destroyed

It's as simple as

```Java
getMyObservable()	// each of the other base reactive types are also supported (Single, Maybe)
	.filter(LifecycleBinder.notDestroyed(this))
	...
```

where `this` is your Activity or Fragment. This automatically stops emitting items (downstream from `filter()`) and throws away the LifecycleOwner (your Activity or Fragment) reference as soon as it hits `onDestroy()`. If you're using Observables, you can use `takeWhile()` here instead of `filter()` to tell the stream to complete as soon as the first item is emitted while the LifecycleOwner is destroyed. This calls `onComplete()`, so be careful not to access views there.

Note: Adding `filter()` to a `Single` returns a `Maybe` so you should use a `MaybeObserver` at time of subscribing, remember this if you're wondering why you have a "inferred type is not within it's bounds" error.

#### Make your stream not emit any items until your Activity/Fragment is active so that you don't prematurely access your views

Here you pass in your Observer (or `DisposableObserver`, `MaybeObserver`, `DisposableMaybeObserver`, etc).

```Java
getMyObservable()
	.filter(LifecycleBinder.notDestroyed(this)) // Building on the example from earlier to show the full stream
	.compose(LifecycleBinder.subscribeWhenReady(this, new DisposableObserver<MyReturnedObject>() {

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

Each item that is emitted before your Activity/Fragment is ready is cached. Once it is active, each of the cached items are emitted in-order. This means all of your streams work is still done while the Activity/Fragment is setting up, but waits until it is active before subscribing where you would access your views.

You can also add `takeLast(1)` above the `compose()` call to have your Observable only emit the latest cached item upon subscription as opposed to all of them.

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
