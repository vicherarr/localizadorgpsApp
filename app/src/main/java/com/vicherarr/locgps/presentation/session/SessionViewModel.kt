package com.vicherarr.locgps.presentation.session

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vicherarr.locgps.domain.model.AuthSession
import com.vicherarr.locgps.domain.usecase.LogoutUseCase
import com.vicherarr.locgps.domain.usecase.ObserveSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue

class SessionViewModel(
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SessionUiState())
    val state: StateFlow<SessionUiState> = _state

    init {
        viewModelScope.launch {
            observeSessionUseCase().collectLatest { session ->
                _state.update { it.copy(isLoading = false, session = session) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}

data class SessionUiState(
    val isLoading: Boolean = true,
    val session: AuthSession? = null
) {
    val isAuthenticated: Boolean get() = session != null
}

@Composable
fun SessionViewModel.collectSessionState(): SessionUiState {
    val uiState by this.state.collectAsStateWithLifecycle()
    return uiState
}
