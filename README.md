Readme is very much a work in progress at the moment.

## Lifecycle-aware RxJava

With the introduction to Android Architecture Components, Google added `LiveData` to their Lifecycle component which is a very simple observable, but takes advantage of a new API in Activities and Fragments: LifecycleOwner, which has lifecycle state callbacks. Because of this, LiveData is lifecycle-aware, meaning that once the Activity/Fragment is destroyed, so is the LiveData automatically. Goodbye memory leaks.

This library lets RxJava leverage the same API, making your reactive streams life-cycle aware with Android's first-party lifecycle API. Right now this is with the use of Google's new `LifecycleActivity` and `LifecycleFragment`, but very soon they are going to bake the LifecycleOwner API directly into the support library's Activity/Fragment, meaning using this library is and will be a piece of cake.

## Examples

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

Lastly, it also works with the other base reactive types.

```Java
// Single
getMyObservable()
  .compose(LifecycleComposer.Single.bindLifeCycle(this))
  ...
         
// Completable
getMyObservable()
  .compose(LifecycleComposer.Completable.bindLifeCycle(this))
  ...
         
// Maybe
getMyObservable()
  .compose(LifecycleComposer.Maybe.bindLifeCycle(this))
  ...
```

## To use
Right now the library is in its early stages so it isn't hosted anywhere. If there's enough positive feedback, that will happen soon. For now, clone this to the same directory that your project is at (not inside your project, this is just so that the git repos are seperate), then change your settings.gradle to

```
include ':app', ':lifecycleawarerx'
project(':lifecycleawarerx').projectDir = new File(settingsDir, '../lifecycleawarerx')
```

And add this to your build.gradle's dependencies block:

```
compile project(path: ':lifecycleawarerx')
```
