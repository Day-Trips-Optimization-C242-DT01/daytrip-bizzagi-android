
package com.bizzagi.daytrip.data.retrofit.response.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bizzagi.daytrip.data.retrofit.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthenticationViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<com.bizzagi.daytrip.data.Result<RegisterResponse>>()
    val registerResult: LiveData<com.bizzagi.daytrip.data.Result<RegisterResponse>> get() = _registerResult

    private val _loginResult = MutableLiveData<com.bizzagi.daytrip.data.Result<LoginResponse>>()
    val loginResult: LiveData<com.bizzagi.daytrip.data.Result<LoginResponse>> get() = _loginResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.registerUser(name, email, password)
            _registerResult.postValue(result)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.loginUser(email, password)
            _loginResult.postValue(result)
        }
    }

    fun saveSessionData(userData: UserData) = viewModelScope.launch {
        authRepository.saveSession(userData)
    }

    fun getSession(): LiveData<UserData> {
        return authRepository.getSession().asLiveData()
    }
}