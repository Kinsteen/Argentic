package fr.kinsteen.argentic.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import fr.kinsteen.argentic.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreUtils(private val context: Context) {
    companion object {
        val SELECTED_ROLL = intPreferencesKey("selected_roll")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val selectedRoll: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_ROLL] ?: 0
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE] ?: true
    }

    suspend fun saveSelectedRoll(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_ROLL] = value
        }
    }

    suspend fun saveDarkMode(value: (Boolean) -> Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = value(preferences[DARK_MODE] ?: true)
        }
    }
}