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

    private val _uid = MutableStateFlow<String?>(null)
    val uid: StateFlow<String?> = _uid

    fun login(email: String, pass: String, tipo: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val result = repo.login(email, pass, tipo)
            _loading.value = false

            if (result.isSuccess) {
                _uid.value = result.getOrNull()
                _success.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    // Adicionado o parâmetro matricula
    fun register(
        nome: String,
        email: String,
        pass: String,
        tipo: String,
        profileData: Map<String, Any>? = null,
        matricula: String? = null
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val isCanteen = tipo.equals("cantina", ignoreCase = true) || tipo.equals("CANTEEN", ignoreCase = true)
            val tipoToSave = if (isCanteen) "CANTEEN" else tipo

            // Passando matricula para o repositório
            val result = repo.register(nome, email, pass, tipoToSave, matricula = matricula)
            if (result.isSuccess) {
                val newUid = result.getOrNull() ?: ""
                if (profileData != null) {
                    try {
                        val collectionName = if (isCanteen) "users" else "profiles"
                        repo.saveProfile(newUid, profileData, collectionName)
                    } catch (e: Exception) {
                        _loading.value = false
                        _error.value = "Conta criada, mas falha ao salvar perfil: ${e.message}"
                        return@launch
                    }
                }
                _uid.value = newUid
                _success.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _loading.value = false
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
        _uid.value = null
    }
}
