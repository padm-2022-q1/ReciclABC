package br.edu.ufabc.reciclabc.utils.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations

// Ref https://stackoverflow.com/a/71511353

fun <T, R> LiveData<T>.map(action: (t: T) -> R): LiveData<R> =
    Transformations.map(this, action)

fun <T1, T2, R> LiveData<T1>.combine(
    liveData: LiveData<T2>,
    action: (t1: T1?, t2: T2?) -> R
): LiveData<R> =
    MediatorLiveData<Pair<T1?, T2?>>().also { med ->
        med.addSource(this) { med.value = it to med.value?.second }
        med.addSource(liveData) { med.value = med.value?.first to it }
    }.map { action(it.first, it.second) }
