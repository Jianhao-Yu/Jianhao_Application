package com.example.jianhao_application.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jianhao_application.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val apiService = RetrofitInstance.api

    fun registerUser(name: String, email: String, password: String, onResult: (UserResponse?) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response: Response<UserResponse> = apiService.registerUser(RegisterPayload(name, email, password))
                if (response.isSuccessful) {
                    val user = response.body()!!
                    saveUserSession(getApplication(), user.id, user.token)
                    _authState.value = AuthState.Success(user)
                    onResult(user)
                } else {
                    _authState.value = AuthState.Error("Registration failed: ${response.errorBody()?.string()}")
                    onResult(null)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("An error occurred: ${e.message}")
                onResult(null)
            }
        }
    }

    fun loginUser(email: String, password: String, onResult: (UserResponse?) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response: Response<UserResponse> = apiService.loginUser(LoginPayload(email, password))
                if (response.isSuccessful) {
                    val user = response.body()!!
                    saveUserSession(getApplication(), user.id, user.token)
                    _authState.value = AuthState.Success(user)
                    onResult(user)
                } else {
                    _authState.value = AuthState.Error("Login failed: ${response.errorBody()?.string()}")
                    onResult(null)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("An error occurred: ${e.message}")
                onResult(null)
            }
        }
    }

    fun clearState() {
        _authState.value = AuthState.Idle
    }
}
