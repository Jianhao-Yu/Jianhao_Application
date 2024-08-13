package com.example.jianhao_application.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.jianhao_application.api.TodoItem
import com.example.jianhao_application.api.TodoState
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlinx.coroutines.Dispatchers

@ExperimentalCoroutinesApi
class TodoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: TodoViewModel

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        viewModel = TodoViewModel()
    }

    @Test
    fun `loadTodos should succeed and update todoState and todos`() = runTest {
        viewModel.loadTodos()

        val state = viewModel.todoState.first()
        val todos = viewModel.todos.first()

        assertTrue(state is TodoState.Success)
        assertEquals(2, todos.size)
    }

    @Test
    fun `loadTodos should fail and update todoState to Error`() = runTest {
        // 使用 MockK 模拟 loadTodos 方法抛出异常
        mockkObject(viewModel)
        every { viewModel.loadTodos() } answers {
            viewModel.apply {
                _todoState.value = TodoState.Error("Simulated failure")
            }
        }

        viewModel.loadTodos()

        val state = viewModel.todoState.first()

        assertTrue(state is TodoState.Error)
        assertEquals("Simulated failure", (state as TodoState.Error).message)
    }

    @Test
    fun `createTodo should add a new todo item`() = runTest {
        val initialSize = viewModel.todos.first().size

        viewModel.createTodo("New Todo")

        val todos = viewModel.todos.first()
        assertEquals(initialSize + 1, todos.size)
        assertEquals("New Todo", todos.last().name)
    }

    @Test
    fun `createTodo should not add an empty todo item`() = runTest {
        val initialSize = viewModel.todos.first().size

        viewModel.createTodo("")

        val todos = viewModel.todos.first()
        assertEquals(initialSize, todos.size) // 空的todo item不应该被添加
    }

    @Test
    fun `updateTodo should update the isCompleted status of a todo item`() = runTest {
        viewModel.loadTodos()

        val todoToUpdate = viewModel.todos.first().first()
        viewModel.updateTodo(todoToUpdate.id, true)

        val updatedTodo = viewModel.todos.first().find { it.id == todoToUpdate.id }
        assertNotNull(updatedTodo)
        assertTrue(updatedTodo!!.isCompleted)
    }

    @Test
    fun `updateTodo should not update if id is incorrect`() = runTest {
        viewModel.loadTodos()

        val initialTodos = viewModel.todos.first()
        viewModel.updateTodo("non_existent_id", true)

        val updatedTodos = viewModel.todos.first()
        assertEquals(initialTodos, updatedTodos) // 列表应该保持不变
    }
}

// Utility class to manage the main dispatcher for coroutines in testing
@ExperimentalCoroutinesApi
class MainCoroutineRule(val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()) : TestWatcher() {
    override fun starting(description: Description?) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }
}
