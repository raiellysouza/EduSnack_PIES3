package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<String?>(null)
    val authState = _authState.asStateFlow()

    fun register(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.registerUser(email, password)
            _authState.value = result.exceptionOrNull()?.message ?: "registered"
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.loginUser(email, password)
            _authState.value = result.exceptionOrNull()?.message ?: "logged_in"
        }
    }

    fun logout() = repository.logoutUser()
}
