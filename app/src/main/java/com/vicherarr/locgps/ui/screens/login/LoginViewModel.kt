package com.vicherarr.locgps.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vicherarr.locgps.core.AppContainer
import com.vicherarr.locgps.domain.usecase.LoginUseCase
import com.vicherarr.locgps.domain.usecase.ObserveSessionUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel(appContainer: AppContainer) : ViewModel() {

    private val loginUseCase = LoginUseCase(appContainer.authRepository)
    private val observeSessionUseCase = ObserveSessionUseCase(appContainer.authRepository)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var sessionJob: Job? = null

    init {
        sessionJob = viewModelScope.launch {
            observeSessionUseCase().collectLatest { session ->
                _uiState.value = _uiState.value.copy(isLoggedIn = session != null && !session.isExpired)
            }
        }
    }

    fun onUsernameChanged(value: String) {
        _uiState.value = _uiState.value.copy(username = value, errorMessage = null)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun login() {
        if (_uiState.value.username.isBlank() || _uiState.value.password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Ingresa usuario y contraseña válidos")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                loginUseCase(_uiState.value.username.trim(), _uiState.value.password)
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Error al iniciar sesión"
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(isLoading = false, password = "")
        }
    }

    override fun onCleared() {
        super.onCleared()
        sessionJob?.cancel()
    }
}
