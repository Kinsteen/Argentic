package fr.kinsteen.argentic

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class serStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userToken")
        private val PHOTOS_TAKEN = intPreferencesKey("photos_taken")
    }

    val getPhotosTaken: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PHOTOS_TAKEN] ?: 0
    }

    suspend fun savePhotosTaken(token: Int) {
        context.dataStore.edit { preferences ->
            preferences[PHOTOS_TAKEN] = token
        }
    }
}