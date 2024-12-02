package com.bizzagi.daytrip.utils

sealed class RResult<out R> private constructor() {
    data class Success<out T>(val data : T) : RResult<T>()
    data class Error(val error : String) : RResult<Nothing>()
    data object Loading : RResult<Nothing>()
}