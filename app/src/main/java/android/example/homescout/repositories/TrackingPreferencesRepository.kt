package android.example.homescout.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject



class TrackingPreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>){

    private object PreferenceKeys {
        val IS_TRACKING_ENABLED = booleanPreferencesKey("is_tracking_enabled")
        val DISTANCE = floatPreferencesKey("distance")
        val TIME_IN_MIN = floatPreferencesKey("time_in_min")
        val OCCURRENCES = floatPreferencesKey("occurrences")
    }


    val isTrackingEnabled = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.IS_TRACKING_ENABLED] ?: false
    }

    val distance = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.DISTANCE] ?: 200.0f
    }

    val timeInMin = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.TIME_IN_MIN] ?: 1.0f
    }

    val occurrences = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.OCCURRENCES] ?: 4.0f
    }

    suspend fun updateIsTrackingEnabled(isTrackingEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_TRACKING_ENABLED] = isTrackingEnabled
        }
    }

    suspend fun updateDistance(distance: Float) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.DISTANCE] = distance
        }
    }

    suspend fun updateTimeInMin(timeInMin: Float) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.TIME_IN_MIN] = timeInMin
        }
    }

    suspend fun updateOccurrences(occurrences: Float) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.OCCURRENCES] = occurrences
        }
    }


}
