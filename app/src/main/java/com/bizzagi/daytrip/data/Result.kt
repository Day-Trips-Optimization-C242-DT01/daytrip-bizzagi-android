package com.bizzagi.daytrip.data

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    data class Loading(val isLoading: Boolean) : Result<Nothing>()
}