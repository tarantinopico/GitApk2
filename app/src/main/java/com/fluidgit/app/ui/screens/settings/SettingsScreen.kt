package com.fluidgit.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.fluidgit.app.domain.repository.SettingsRepository
import com.fluidgit.app.ui.components.FluidCard
import com.fluidgit.app.ui.theme.CyanNeon
import com.fluidgit.app.ui.theme.HotMagenta
import com.fluidgit.app.ui.theme.Slate400
import com.fluidgit.app.AppViewModelProvider

data class SettingsUiState(
    val isLiquidLight: Boolean = false,
    val isAmoled: Boolean = false,
    val isBiometricLockEnabled: Boolean = false
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.isLiquidLight,
        settingsRepository.isAmoled,
        settingsRepository.isBiometricLockEnabled
    ) { light, amoled, biometric ->
        SettingsUiState(light, amoled, biometric)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun toggleLiquidLight(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setLiquidLight(enabled) }
    }

    fun toggleAmoled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setAmoled(enabled) }
    }

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setBiometricLock(enabled) }
    }
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider)
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        androidx.compose.material3.Text(
            text = "Settings",
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        FluidCard {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SettingsToggleRow(
                    title = "Liquid Light Theme",
                    subtitle = "Switch to bright glassy visuals",
                    checked = state.isLiquidLight,
                    onCheckedChange = { viewModel.toggleLiquidLight(it) }
                )
                
                SettingsToggleRow(
                    title = "AMOLED Dark Mode",
                    subtitle = "Pure black backgrounds for battery",
                    checked = state.isAmoled,
                    onCheckedChange = { viewModel.toggleAmoled(it) },
                    enabled = !state.isLiquidLight
                )

                SettingsToggleRow(
                    title = "Biometric Lock",
                    subtitle = "Require fingerprint/face to open app",
                    checked = state.isBiometricLockEnabled,
                    onCheckedChange = { viewModel.toggleBiometric(it) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        FluidCard {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SettingsRow(title = "SSH Keys", subtitle = "Manage SSH identities")
                SettingsRow(title = "Labs", subtitle = "Experimental features")
                SettingsRow(title = "About", subtitle = "com.fluidgit.app v1.0")
            }
        }
    }
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground.copy(alpha = if (enabled) 1f else 0.5f),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = Slate400.copy(alpha = if (enabled) 1f else 0.5f),
                fontSize = 12.sp
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = CyanNeon,
                uncheckedThumbColor = Slate400,
                uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
            )
        )
    }
}

@Composable
fun SettingsRow(
    title: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = Slate400,
                fontSize = 12.sp
            )
        }
    }
}
