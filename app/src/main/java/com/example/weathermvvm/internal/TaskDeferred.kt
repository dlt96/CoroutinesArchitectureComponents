package com.example.weathermvvm.internal

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

fun <T> Task<T>.asDeferred(): Deferred<T> {
    //extension fun of Task, called asDeferred, with one parameter <T> and returns a Deferred
    val deferred = CompletableDeferred<T>()

    this.addOnSuccessListener { result->
        deferred.complete(result)
    }

    this.addOnFailureListener { exception ->
        deferred.completeExceptionally(exception)
    }

    return deferred
}