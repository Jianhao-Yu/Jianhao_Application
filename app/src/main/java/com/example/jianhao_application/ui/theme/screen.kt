package com.example.jianhao_application.ui.theme

sealed class Screen {
    object Login : Screen()
    object CreateAccount : Screen()
    object TodoList : Screen()
}
