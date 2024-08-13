package com.example.jianhao_application.viewModel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.jianhao_application.api.ApiService
import com.example.jianhao_application.api.RetrofitInstance
import com.example.jianhao_application.api.UserResponse
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RegisterViewModel
    private lateinit var application: Application
    private val apiService = mockk<ApiService>()

    @Before
    fun setup() {
        application = mockk(relaxed = true)
        viewModel = RegisterViewModel(application)
        Dispatchers.setMain(StandardTestDispatcher())
        mockkObject(RetrofitInstance)
        every { RetrofitInstance.api } returns apiService
    }

    @Test
    fun `registerUser should succeed when API call is successful`() = runTest {
        val response = UserResponse("id", "token")
        coEvery { apiService.registerUser(any()) } returns Response.success(response)
        val onResult = mockk<(UserResponse?) -> Unit>(relaxed = true)

        viewModel.registerUser("name", "email", "password", onResult)

        val result = viewModel.registerState.first()
        assertEquals(RegisterState.Success(response), result)
        verify { onResult(response) }
    }

    @Test
    fun `registerUser should fail when API call is unsuccessful`() = runTest {
        coEvery { apiService.registerUser(any()) } returns Response.error(400, mockk(relaxed = true))
        val onResult = mockk<(UserResponse?) -> Unit>(relaxed = true)

        viewModel.registerUser("name", "email", "password", onResult)

        val result = viewModel.registerState.first()
        assert(result is RegisterState.Error)
        verify { onResult(null) }
    }

    @Test
    fun `clearState should reset registerState to Idle`() = runTest {
        viewModel.clearState()

        val result = viewModel.registerState.first()
        assertEquals(RegisterState.Idle, result)
    }

    @Test
    fun `clearState should reset registerState from Success to Idle`() = runTest {
        val response = UserResponse("id", "token")
        // 模拟成功状态
        coEvery { apiService.registerUser(any()) } returns Response.success(response)
        viewModel.registerUser("name", "email", "password", mockk(relaxed = true))
        val successState = viewModel.registerState.first()
        assertEquals(RegisterState.Success(response), successState)

        // 测试 clearState 后状态变为 Idle
        viewModel.clearState()
        val idleState = viewModel.registerState.first()
        assertEquals(RegisterState.Idle, idleState)
    }
}
