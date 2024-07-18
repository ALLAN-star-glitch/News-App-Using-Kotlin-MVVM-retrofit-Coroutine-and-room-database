package com.example.thenewsapp.util

sealed class Resource<T>(
    var message: String? = null,
    var data: T? = null
) {
    class Success<T>(data: T): Resource<T>(data = data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data=data, message=message)
    class Loading<T>: Resource<T>()

}