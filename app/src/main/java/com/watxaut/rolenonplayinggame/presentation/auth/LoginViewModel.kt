package com.watxaut.rolenonplayinggame.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watxaut.rolenonplayinggame.core.lifecycle.OfflineSimulationManager
import com.watxaut.rolenonplayinggame.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSignUpMode: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val offlineSimulationManager: OfflineSimulationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun toggleMode() {
        _uiState.update { it.copy(isSignUpMode = !it.isSignUpMode, errorMessage = null) }
    }

    fun signIn(onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (!validateInput(email, password)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.signInWithEmail(email, password)

            result.onSuccess {
                // Cache user ID for offline simulation tracking
                offlineSimulationManager.cacheCurrentUserId()

                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Sign in failed. Please check your credentials."
                    )
                }
            }
        }
    }

    fun signUp(onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (!validateInput(email, password)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.signUpWithEmail(email, password)

            result.onSuccess {
                // Cache user ID for offline simulation tracking
                offlineSimulationManager.cacheCurrentUserId()

                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Sign up failed. Please try again."
                    )
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        when {
            email.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Email cannot be empty") }
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _uiState.update { it.copy(errorMessage = "Please enter a valid email address") }
                return false
            }
            password.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Password cannot be empty") }
                return false
            }
            password.length < 6 -> {
                _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
                return false
            }
        }
        return true
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
