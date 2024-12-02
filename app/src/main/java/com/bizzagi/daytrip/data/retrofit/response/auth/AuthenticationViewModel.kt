package com.bizzagi.daytrip.data.retrofit.response.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizzagi.daytrip.data.local.pref.UserModel
import com.bizzagi.daytrip.data.retrofit.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun register(name : String, email: String, password: String, repassword: String) = authRepository.registerUser(name, email, password, repassword)

    fun login(email : String, password : String) = authRepository.loginUser(email, password)

    fun saveSessionData(userData : UserModel) = viewModelScope.launch {
        authRepository.saveSession(userData)
    }
}