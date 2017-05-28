package com.waylonbrown.lifecycleawarerx;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

public class LifecycleTest {

	private TestLifecycleOwner lifecycleOwner;

	@Before
	public void setup() {
		this.lifecycleOwner = new TestLifecycleOwner();
	}
	
	@Test
	public void streamDisposesAndLifecycleOwnerReferenceRemovedWhenLifecycleDestroyed() throws Exception {
		Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);

		final boolean[] methodOnViewCalled = {false};
		observable.compose(LifecycleBinder.disposeIfDestroyed(lifecycleOwner))
//			.takeWhile(new LifecyclePredicate(LifecycleBinder.disposeIfDestroyed(lifecycleOwner), lifecycleOwner))
			.subscribeWith(new DisposableObserver() {
				@Override
				public void onNext(final Object value) {
					methodOnViewCalled[0] = true;
				}

				@Override
				public void onError(final Throwable e) {
				}

				@Override
				public void onComplete() {
				}
			});
		
		lifecycleOwner.setState(Lifecycle.State.DESTROYED);
		
		// Need to wait a second to give it time to potentially fail
		TimeUnit.SECONDS.sleep(3);
		assertEquals(false, methodOnViewCalled[0]);
		// TODO: make sure lifecycleowner was thrown away
	}
	
	static class TestLifecycle extends Lifecycle {
		
		private State state = State.STARTED;
		private RxLifecycleObserver observer;

		@Override
		public void addObserver(final LifecycleObserver observer) {
			this.observer = (RxLifecycleObserver)observer;
		}

		@Override
		public void removeObserver(final LifecycleObserver observer) {
		}

		@Override
		public State getCurrentState() {
			return state;
		}
		
		public void setCurrentState(State state) {
			this.state = state;
			// TODO: for now not using observer for disposeondestroy
			if (observer != null) {
				observer.onStateChange();
			}
		}
	}
	
	static class TestLifecycleOwner implements LifecycleOwner {

		private final TestLifecycle lifecycle;

		TestLifecycleOwner() {
			this.lifecycle = new TestLifecycle();
		}
		
		@Override
		public Lifecycle getLifecycle() {
			return lifecycle;
		}

		public void setState(final Lifecycle.State state) {
			lifecycle.setCurrentState(state);
		}
	}
}