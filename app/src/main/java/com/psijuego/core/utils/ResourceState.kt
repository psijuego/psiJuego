package com.psijuego.core.utils

sealed class ResourceState<out T> {
    object Loading : ResourceState<Nothing>()
    data class Success<T>(val data: T) : ResourceState<T>()
    data class Failure(val message: String) : ResourceState<Nothing>()
}
