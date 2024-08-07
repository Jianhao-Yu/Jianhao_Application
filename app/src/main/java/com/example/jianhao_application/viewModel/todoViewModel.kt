package com.example.jianhao_application.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jianhao_application.api.TodoItem
import com.example.jianhao_application.api.TodoState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {
    private val _todoState = MutableStateFlow<TodoState>(TodoState.Idle)
    val todoState: StateFlow<TodoState> = _todoState

    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())
    val todos: StateFlow<List<TodoItem>> = _todos

    init {
        // 初始化时加载 todos
        loadTodos()
    }

    fun loadTodos() {
        viewModelScope.launch {
            _todoState.value = TodoState.Loading
            try {
                // 模拟从服务器加载 todo 列表
                val todoList = listOf(
                    TodoItem("1", "Sample Todo 1", false),
                    TodoItem("2", "Sample Todo 2", false)
                )
                _todos.value = todoList
                _todoState.value = TodoState.Success(todoList)
            } catch (e: Exception) {
                _todoState.value = TodoState.Error("Failed to load todos")
            }
        }
    }

    fun createTodo(name: String) {
        viewModelScope.launch {
            val newTodo = TodoItem("id", name, false)
            _todos.value = _todos.value + newTodo
        }
    }

    fun updateTodo(id: String, isCompleted: Boolean) {
        viewModelScope.launch {
            _todos.value = _todos.value.map {
                if (it.id == id) it.copy(isCompleted = isCompleted) else it
            }
        }
    }
}
