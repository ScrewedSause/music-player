package com.example.musicplayer.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.musicplayer.ui.theme.AppThemes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val THEME_KEY = stringPreferencesKey("selected_theme_id")

    val selectedThemeId: Flow<String> =
        context.dataStore.data.map { it[THEME_KEY] ?: AppThemes.Dark.id }

    suspend fun setThemeId(id: String) {
        context.dataStore.edit { it[THEME_KEY] = id }
    }
}
