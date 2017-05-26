[![](https://jitpack.io/v/WaylonBrown/LifecycleAwareRx.svg)](https://jitpack.io/#WaylonBrown/LifecycleAwareRx)

## Lifecycle-aware RxJava2

With the introduction to Android Architecture Components, Google added `LiveData` to their Lifecycle component which is a very simple observable, but it is "lifecycle-aware" - it takes advantage of a new API in Activities and Fragments: LifecycleOwner, which has lifecycle state callbacks. Once the Activity/Fragment is destroyed, so is the LiveData automatically. Goodbye memory leaks.

LifecycleAwareRx is a lightweight library that lets RxJava leverage the same API, making your reactive streams life-cycle aware with Android's first-party lifecycle API. Right now this is with the use of Google's new `LifecycleActivity` and `LifecycleFragment`, but very soon they are going to bake the LifecycleOwner API directly into the support library's Activity/Fragment, meaning you'll be able to use this library with your current Activities and Fragments with no changes.

## How to use

To reiterate from above, for now you need to have your Activities and Fragments that want to use this extend `LifecycleActivity` and `LifecycleFragment`. Then it's as simple as

```Java
getMyObservable()
  .compose(LifecycleComposer.bindLifeCycle(this))
                ...
```

where `this` is your Activity or Fragment. This automatically disposes of the observable and throws away the LifecycleOwner (your Activity or Fragment) reference as soon as it hits `onDestroy()`. You can instead customize which state in the lifecycle you want it to terminate with

```Java
getMyObservable()
  .compose(LifecycleComposer.disposeOnLifecycleEvent(this, Lifecycle.State.RESUMED))
                ...
```

Using `LifecycleComposer` will return the correct transformer given your stream's base reactive type, whether it's an **Observable**, **Single**, **Maybe**, or **Completable**.

## New library features

Right now when the Activity/Fragment is destroyed, the stream is disposed and the LifecycleOwner reference is thrown away. Currently I'm working on only starting the stream when the Activity/Fragment is started, just as is currently done in `LiveData`. **I am very open to any new feature suggestions, so please add Github issues if there's something that should be added to get to feature parity with `LiveData`.**

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
  compile 'com.github.WaylonBrown:LifecycleAwareRx:0.1'
}
```
