package com.example.jianhao_application.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.jianhao_application.api.ApiService
import com.example.jianhao_application.api.RetrofitInstance
import com.example.jianhao_application.api.UserResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.mockk.*
import retrofit2.Response
import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AuthViewModel
    private lateinit var application: Application
    private val apiService = mockk<ApiService>()

    @Before
    fun setup() {
        application = mockk(relaxed = true)
        viewModel = AuthViewModel(application)
        Dispatchers.setMain(StandardTestDispatcher())
        mockkObject(RetrofitInstance)
        every { RetrofitInstance.api } returns apiService
    }

    @Test
    fun `registerUser should succeed when API call is successful`() = runTest {
        val response = UserResponse("id", "token")
        coEvery { apiService.registerUser(any()) } returns Response.success(response)

        viewModel.registerUser("name", "email", "password") {}

        val result = viewModel.authState.first() // 使用挂起函数在协程内调用
        assertEquals(AuthState.Success(response), result)
    }

    @Test
    fun `registerUser should fail when API call is unsuccessful`() = runTest {
        coEvery { apiService.registerUser(any()) } returns Response.error(400, mockk(relaxed = true))

        viewModel.registerUser("name", "email", "password") {}

        val result = viewModel.authState.first() // 使用挂起函数在协程内调用
        assert(result is AuthState.Error)
    }

    @Test
    fun `loginUser should succeed when API call is successful`() = runTest {
        val response = UserResponse("id", "token")
        coEvery { apiService.loginUser(any()) } returns Response.success(response)

        viewModel.loginUser("email", "password") {}

        val result = viewModel.authState.first() // 使用挂起函数在协程内调用
        assertEquals(AuthState.Success(response), result)
    }

    @Test
    fun `loginUser should fail when API call is unsuccessful`() = runTest {
        coEvery { apiService.loginUser(any()) } returns Response.error(400, mockk(relaxed = true))

        viewModel.loginUser("email", "password") {}

        val result = viewModel.authState.first() // 使用挂起函数在协程内调用
        assert(result is AuthState.Error)
    }

    @Test
    fun `clearState should reset authState to Idle`() = runTest {
        viewModel.clearState()

        val result = viewModel.authState.first() // 使用挂起函数在协程内调用
        assertEquals(AuthState.Idle, result)
    }

    @Test
    fun `clearState should reset authState from Success to Idle`() = runTest {
        val response = UserResponse("id", "token")
        // 模拟成功状态
        coEvery { apiService.registerUser(any()) } returns Response.success(response)
        viewModel.registerUser("name", "email", "password") {}
        val successState = viewModel.authState.first()
        assertEquals(AuthState.Success(response), successState)

        // 测试 clearState 后状态变为 Idle
        viewModel.clearState()
        val idleState = viewModel.authState.first()
        assertEquals(AuthState.Idle, idleState)
    }
}
