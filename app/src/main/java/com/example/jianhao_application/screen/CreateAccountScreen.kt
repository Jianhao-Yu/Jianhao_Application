package com.example.jianhao_application.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.jianhao_application.viewModel.RegisterViewModel
import com.example.jianhao_application.viewModel.RegisterState

@Composable
fun CreateAccountScreen(
    registerViewModel: RegisterViewModel,
    onRegisterSuccess: (String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") } // 添加 name 状态
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val registerState by registerViewModel.registerState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }) // 添加 name 输入框
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            registerViewModel.registerUser(name, email, password) { response -> // 传递 name, email, 和 password 参数
                if (response != null) {
                    onRegisterSuccess(response.id, response.token)
                } else {
                    Toast.makeText(context, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onNavigateToLogin) { Text("Log In") }

        when (registerState) {
            is RegisterState.Loading -> CircularProgressIndicator()
            is RegisterState.Error -> {
                val error = (registerState as RegisterState.Error).message
                Text(text = error, color = MaterialTheme.colors.error)
            }
            is RegisterState.Success -> {
                val user = (registerState as RegisterState.Success).user
                onRegisterSuccess(user.id, user.token)
            }
            else -> {}
        }
    }
}
