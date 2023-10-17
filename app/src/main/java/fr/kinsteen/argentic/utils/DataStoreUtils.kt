package fr.kinsteen.argentic.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import fr.kinsteen.argentic.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreUtils(private val context: Context) {
    companion object {
        val SELECTED_ROLL = intPreferencesKey("selected_roll")
    }

    val getSelectedRoll: Flow<Int> = context.dataStore.data.map { preferences ->
            preferences[SELECTED_ROLL] ?: 0
        }

    suspend fun saveSelectedRoll(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_ROLL] = value
        }
    }
}