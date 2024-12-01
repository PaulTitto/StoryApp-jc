package com.mosalab.submissionpaai

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(context: Context) {

    private val dataStore = UserPreferencesDataStore.getDataStore()
    private val tokenKey = stringPreferencesKey("token")

    val token: Flow<String?> = dataStore.data.map { preferences ->
        preferences[tokenKey]
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }


    object UserPreferencesDataStore {
        private lateinit var dataStore: DataStore<Preferences>

        fun initialize(context: Context) {
            if (!::dataStore.isInitialized) {
                dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile("user_preferences")
                }
            }
        }

        fun getDataStore(): DataStore<Preferences> {
            if (!::dataStore.isInitialized) {
                throw IllegalStateException("DataStore not initialized. Call initialize() first.")
            }
            return dataStore
        }
    }
}
