package com.example.jianhao_application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jianhao_application.ui.theme.JianhaoApplicationTheme
import com.example.jianhao_application.screen.CreateAccountScreen
import com.example.jianhao_application.screen.LoginScreen
import com.example.jianhao_application.screen.TodoListScreen
import com.example.jianhao_application.ui.theme.Screen
import com.example.jianhao_application.viewModel.AuthViewModel
import com.example.jianhao_application.viewModel.RegisterViewModel
import com.example.jianhao_application.viewModel.TodoViewModel

@OptIn(ExperimentalMaterialApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JianhaoApplicationTheme {
                val authViewModel: AuthViewModel = viewModel()
                val registerViewModel: RegisterViewModel = viewModel()
                val todoViewModel: TodoViewModel = viewModel()
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
                var userId by remember { mutableStateOf<String?>(null) }
                var token by remember { mutableStateOf<String?>(null) }
                val context = LocalContext.current

                when (currentScreen) {
                    is Screen.Login -> LoginScreen(
                        onLogin = { email, password ->
                            userId = "testUserId" // Dummy userId for testing
                            token = "testToken" // Dummy token for testing
                            currentScreen = Screen.TodoList
                        },
                        onNavigateToCreateAccount = {
                            currentScreen = Screen.CreateAccount
                        }
                    )
                    is Screen.CreateAccount -> CreateAccountScreen(
                        registerViewModel = registerViewModel,
                        onRegisterSuccess = { id, tok ->
                            userId = id
                            token = tok
                            currentScreen = Screen.TodoList
                        },
                        onNavigateToLogin = {
                            currentScreen = Screen.Login
                        }
                    )
                    is Screen.TodoList -> {
                        if (userId != null && token != null) {
                            TodoListScreen(todoViewModel, userId!!)
                        }
                    }
                }
            }
        }
    }
}