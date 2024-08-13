package com.example.jianhao_application.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jianhao_application.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val user: UserResponse) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> get() = _registerState

    private val apiService = RetrofitInstance.api

    fun registerUser(name: String, email: String, password: String, onResult: (UserResponse?) -> Unit) { // 添加 name 参数
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val response: Response<UserResponse> = apiService.registerUser(RegisterPayload(name, email, password)) // 传递 name 参数
                if (response.isSuccessful) {
                    val user = response.body()!!
                    saveUserSession(getApplication(), user.id, user.token)
                    _registerState.value = RegisterState.Success(user)
                    onResult(user)
                    Log.d("RegisterViewModel", "Registration successful: $user")
                } else {
                    val errorMessage = "Registration failed: ${response.errorBody()?.string()}"
                    _registerState.value = RegisterState.Error(errorMessage)
                    onResult(null)
                    Log.e("RegisterViewModel", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "An error occurred: ${e.message}"
                _registerState.value = RegisterState.Error(errorMessage)
                onResult(null)
                Log.e("RegisterViewModel", errorMessage, e)
            }
        }
    }

    fun clearState() {
        _registerState.value = RegisterState.Idle
    }
}
