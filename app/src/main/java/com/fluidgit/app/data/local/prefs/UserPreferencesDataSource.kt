package com.fluidgit.app.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    val defaultAuthorName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[AUTHOR_NAME_KEY]
    }

    val defaultAuthorEmail: Flow<String?> = dataStore.data.map { preferences ->
        preferences[AUTHOR_EMAIL_KEY]
    }

    val defaultRemote: Flow<String?> = dataStore.data.map { preferences ->
        preferences[DEFAULT_REMOTE_KEY]
    }

    val themePreference: Flow<String?> = dataStore.data.map { preferences ->
        preferences[THEME_KEY]
    }

    suspend fun setDefaultAuthor(name: String, email: String) {
        dataStore.edit { preferences ->
            preferences[AUTHOR_NAME_KEY] = name
            preferences[AUTHOR_EMAIL_KEY] = email
        }
    }

    suspend fun setDefaultRemote(remote: String) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_REMOTE_KEY] = remote
        }
    }

    suspend fun setThemePreference(theme: String) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }

    companion object {
        private val AUTHOR_NAME_KEY = stringPreferencesKey("author_name")
        private val AUTHOR_EMAIL_KEY = stringPreferencesKey("author_email")
        private val DEFAULT_REMOTE_KEY = stringPreferencesKey("default_remote")
        private val THEME_KEY = stringPreferencesKey("theme")
    }
}
