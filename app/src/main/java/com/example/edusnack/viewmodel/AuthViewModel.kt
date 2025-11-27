package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val result = repo.login(email, pass)
            _loading.value = false

            if (result.isSuccess) _success.value = true
            else _error.value = result.exceptionOrNull()?.message
        }
    }

    fun register(nome: String, email: String, pass: String, tipo: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val result = repo.register(nome, email, pass, tipo)
            _loading.value = false

            if (result.isSuccess) _success.value = true
            else _error.value = result.exceptionOrNull()?.message
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val result = repo.resetPassword(email)
            _loading.value = false

            if (result.isSuccess) _success.value = true
            else _error.value = result.exceptionOrNull()?.message
        }
    }

    fun clearState() {
        _success.value = false
        _error.value = null
    }
}
