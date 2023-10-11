package com.conduent.nationalhighways.utils.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, action: (t: T) -> Unit) {
    liveData.observe(this) { it?.let { t -> action(t) } }
}

fun <T> LifecycleOwner.removeObserve(liveData: LiveData<T>, action: (t: T) -> Unit) {
    liveData.removeObserver(action)
}

fun <T> LifecycleOwner.observeEvent(liveData: LiveData<SingleEvent<T>>, action: (t: SingleEvent<T>) -> Unit) {
    liveData.observe(this) { it?.let { t -> action(t) } }

}

fun <T> LifecycleOwner.observeOnce(liveData: LiveData<T>, action: (t: T) -> Unit) {
    liveData.observeOnce(this) { it?.let { t -> action(t) } }
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}