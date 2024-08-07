package com.example.jianhao_application.api

import androidx.compose.runtime.Composable
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text


sealed interface TodoState {
    @Composable
    fun render()

    object Idle : TodoState {
        @Composable
        override fun render() {
            // Do nothing or show idle state
        }
    }
    object Loading : TodoState {
        @Composable
        override fun render() {
            // Show loading state
            CircularProgressIndicator()
        }
    }
    data class Success(val todos: List<TodoItem>) : TodoState {
        @Composable
        override fun render() {
            // Show success state
            // (This part will be rendered in the main composable function)
        }
    }
    data class Error(val message: String) : TodoState {
        @Composable
        override fun render() {
            // Show error message
            Text(text = message)
        }
    }
}
