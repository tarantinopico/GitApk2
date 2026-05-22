package com.fluidgit.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fluidgit.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ThemeState(
    val isLiquidLight: Boolean = false,
    val isAmoled: Boolean = false,
    val isBiometricLockEnabled: Boolean = false
)

class GlobalViewModel(
    settingsRepository: SettingsRepository
) : ViewModel() {

    val themeState: StateFlow<ThemeState> = combine(
        settingsRepository.isLiquidLight,
        settingsRepository.isAmoled,
        settingsRepository.isBiometricLockEnabled
    ) { light, amoled, biometric ->
        ThemeState(light, amoled, biometric)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeState()
    )
}
