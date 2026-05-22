package com.fluidgit.app.domain.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val THEME_LIQUID_LIGHT = booleanPreferencesKey("theme_liquid_light")
    private val THEME_AMOLED = booleanPreferencesKey("theme_amoled")
    private val BIOMETRIC_LOCK = booleanPreferencesKey("biometric_lock")
    private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

    val isLiquidLight: Flow<Boolean> = dataStore.data.map { it[THEME_LIQUID_LIGHT] ?: false }
    val isAmoled: Flow<Boolean> = dataStore.data.map { it[THEME_AMOLED] ?: false }
    val isBiometricLockEnabled: Flow<Boolean> = dataStore.data.map { it[BIOMETRIC_LOCK] ?: false }
    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }

    suspend fun setLiquidLight(enabled: Boolean) {
        dataStore.edit { it[THEME_LIQUID_LIGHT] = enabled }
    }

    suspend fun setAmoled(enabled: Boolean) {
        dataStore.edit { it[THEME_AMOLED] = enabled }
    }

    suspend fun setBiometricLock(enabled: Boolean) {
        dataStore.edit { it[BIOMETRIC_LOCK] = enabled }
    }
    
    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }
}
