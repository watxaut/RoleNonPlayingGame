package com.watxaut.rolenonplayinggame.presentation.navigation

import androidx.lifecycle.ViewModel
import com.watxaut.rolenonplayinggame.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    val authRepository: AuthRepository
) : ViewModel()
