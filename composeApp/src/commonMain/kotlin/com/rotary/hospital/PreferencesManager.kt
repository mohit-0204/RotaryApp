package com.rotary.hospital


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PreferencesManager {
    suspend fun saveString(key: String, value: String)
    suspend fun saveInt(key: String, value: Int)
    suspend fun saveBoolean(key: String, value: Boolean)
    suspend fun saveFloat(key: String, value: Float)

    fun getString(key: String, default: String): Flow<String>
    fun getInt(key: String, default: Int): Flow<Int>
    fun getBoolean(key: String, default: Boolean): Flow<Boolean>
    fun getFloat(key: String, default: Float): Flow<Float>

    suspend fun clear(key: String)
    suspend fun clearAll()
}


class PreferencesManagerImpl(private val dataStore: DataStore<Preferences>) : PreferencesManager {
    override suspend fun saveString(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    override suspend fun saveInt(key: String, value: Int) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey(key)] = value
        }
    }

    override suspend fun saveBoolean(key: String, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    override suspend fun saveFloat(key: String, value: Float) {
        dataStore.edit { preferences ->
            preferences[floatPreferencesKey(key)] = value
        }
    }

    override fun getString(key: String, default: String): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)] ?: default
        }
    }

    override fun getInt(key: String, default: Int): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[intPreferencesKey(key)] ?: default
        }
    }

    override fun getBoolean(key: String, default: Boolean): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(key)] ?: default
        }
    }

    override fun getFloat(key: String, default: Float): Flow<Float> {
        return dataStore.data.map { preferences ->
            preferences[floatPreferencesKey(key)] ?: default
        }
    }


    override suspend fun clear(key: String) {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
            preferences.remove(intPreferencesKey(key))
            preferences.remove(booleanPreferencesKey(key))
            preferences.remove(floatPreferencesKey(key))
        }
    }

    override suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }

}

expect fun createPreferencesManager(): PreferencesManager