package com.example.jianhao_application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.jianhao_application.ui.theme.JianhaoApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JianhaoApplicationTheme {
                var todoItems by remember { mutableStateOf(listOf<TodoItem>()) }
                val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                    bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
                )
                val coroutineScope = rememberCoroutineScope()

                BottomSheetScaffold(
                    scaffoldState = bottomSheetScaffoldState,
                    topBar = {
                        TopAppBar(title = { Text("Todo") })
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.expand()
                            }
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Todo")
                        }
                    },
                    sheetContent = {
                        AddTodoBottomSheet(
                            onAdd = { name ->
                                todoItems = todoItems + TodoItem(name)
                                coroutineScope.launch {
                                    bottomSheetScaffoldState.bottomSheetState.collapse()
                                }
                            },
                            onDismiss = {
                                coroutineScope.launch {
                                    bottomSheetScaffoldState.bottomSheetState.collapse()
                                }
                            }
                        )
                    },
                    sheetPeekHeight = 0.dp,
                    content = { innerPadding ->
                        TodoList(
                            todoItems = todoItems,
                            onItemCheckedChange = { item, isChecked ->
                                todoItems = todoItems.map {
                                    if (it == item) it.copy(isCompleted = isChecked) else it
                                }
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                )
            }
        }
    }
}

data class TodoItem(val name: String, var isCompleted: Boolean = false)

@Composable
fun TodoItemRow(todoItem: TodoItem, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = todoItem.isCompleted,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = todoItem.name)
    }
}

@Composable
fun TodoList(todoItems: List<TodoItem>, onItemCheckedChange: (TodoItem, Boolean) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(todoItems) { item ->
            TodoItemRow(todoItem = item, onCheckedChange = { isChecked ->
                onItemCheckedChange(item, isChecked)
            })
        }
    }
}

@Composable
fun AddTodoBottomSheet(onAdd: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = screenHeight * 0.5f)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("New Todo") },
            trailingIcon = {
                IconButton(onClick = { text = "" }) {
                    Icon(Icons.Filled.Close, contentDescription = "Clear")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // 设置文本框的高度
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (text.isNotEmpty()) {
                    onAdd(text)
                } else {
                    // Show error
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}